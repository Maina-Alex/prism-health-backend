package com.prismhealth.services;

import com.auth0.jwt.JWT;
import com.prismhealth.Models.*;

import com.prismhealth.config.UwaziiConfig;
import com.prismhealth.dto.Request.Phone;
import com.prismhealth.dto.Request.SignUpRequest;

import com.prismhealth.dto.Request.UpdateForgotPasswordReq;
import com.prismhealth.dto.Request.UwaziiSmsRequest;
import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.*;
import com.prismhealth.security.SecurityConstants;
import com.prismhealth.util.Actions;

import com.prismhealth.util.HelperUtility;
import com.prismhealth.util.LogMessage;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import okhttp3.*;

import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final UserRatingsRepo userRatingsRepo;
    private final UserRolesRepo userRolesRepo;
    private final BCryptPasswordEncoder encoder;
    private final MessageSender messageSender;


    public ResponseEntity<SignUpResponse> authentication(Phone phone) {
        SignUpResponse signUpResponse = new SignUpResponse();
        Users users = userRepository.findOneByPhone(phone.getPhone());
        if (users != null) {
            log.info("phone->" + users.getPhone());
            signUpResponse.setMessage("user already exists");
            return ResponseEntity.badRequest().body(signUpResponse);
        } else {
            String authCode = null;
            try {
                authCode = authService.getAuthentication(phone.getPhone()).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            signUpResponse.setMessage("Create new user..");
            signUpResponse.setAuthCode(authCode);
        }
        return ResponseEntity.ok(signUpResponse);
    }

    public ResponseEntity<SignUpResponse> signUpUser(SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = new SignUpResponse();

        Users thisUsers = userRepository.findOneByPhone(signUpRequest.getPhone());
        if (thisUsers == null) {
            Users users1 = new Users();
            users1.setPassword(encoder.encode(signUpRequest.getPassword()));
            users1.setPhone(signUpRequest.getPhone());
            users1.setEmail(signUpRequest.getEmail());
            users1.setFirstName(signUpRequest.getFirstName());
            users1.setSecondName(signUpRequest.getSecondName());
            users1.setGender(signUpRequest.getGender());
            users1.setDateOfBirth(signUpRequest.getDateOfBirth());
            users1.setLocationName(signUpRequest.getLocation());

            users1.setPositions(signUpRequest.getPositions());

            users1.setPosition(
                    new double[] { users1.getPositions().getLatitude(), users1.getPositions().getLongitude() });
            users1.setEmergencyContact1(null);
            users1.setEmergencyContact2(null);
            users1.setAccountType("USER");
            log.info("Registering new Mobile User:  Id:" + users1.getPhone());

            users1.setVerified(true);
            users1.setBlocked(false);
            users1.setDeleted(false);

            users1 = userRepository.save(users1);
            signUpResponse.setMessage("successfully created");

            UserRoles role = new UserRoles();
            role.setAssignedBy("DEFAULT");
            role.setRole("ROLE_USER");// "ROLE_ADMIN", "ROLE_HELP_SUPPORT", "ROLE_SITE_CONTENT_UPDATER"));
            role.setUserId(users1.getPhone());
            userRolesRepo.save(role);
            log.info("Assigned Default User Role to UserId:" + users1.getPhone());
            sendMessage(users1.getPhone());
            signUpResponse.setUsers(users1);
            return ResponseEntity.ok().body(signUpResponse);
        } else {

            signUpResponse.setMessage("User already exists sign in..");
            return ResponseEntity.badRequest().body(signUpResponse);
        }
    }

    public ResponseEntity<?> updateForgotPassword(UpdateForgotPasswordReq req){
        Users user=userRepository.findOneByPhone(req.getPhone());
        if(user==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        user.setPassword(encoder.encode(req.getPassword()));
        user.setVerificationToken("");
        userRepository.save(user);
        return ResponseEntity.ok().body("User password successfully updated");
    }

    public ResponseEntity<?> forgotPassword(@NonNull Phone phone)  {

       try{
           Users users = userRepository.findOneByPhone(phone.getPhone());
           if (users == null) {
               return new ResponseEntity<>("User with phone number " + phone + " not found", HttpStatus.NOT_FOUND);
           } else {
               log.info("Forgot password request, user email  " + users.getEmail());
               String code = Objects.requireNonNull(forgotPasswordMail(phone.getPhone())).get();
               if (code != null) {
                   users.setVerificationToken(code);
                   userRepository.save(users);
                   return new ResponseEntity<>(code, HttpStatus.OK);
               }
               return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("User password not modified");
           }
       }catch (Exception ex){
           return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("User password not modified");
       }
    }


    private Future<String> forgotPasswordMail(String phone){
        String code=HelperUtility.getConfirmCodeNumber();
       return messageSender.sendMessage(phone,code);
    }


    public String getToken(String phone) {
        Optional<Users> users = Optional.ofNullable(userRepository.findOneByPhone(phone));
        return users.map(value -> JWT.create().withSubject(value.getPhone())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()))).orElse(null);

    }

    public ResponseEntity<?> changePassword(PasswordReset reset) {
        return ResponseEntity.ok(authService.resetPassword(reset.getPassword(), reset.getAuthCode()));
    }

    public Map<String, Integer> getUserRating(String userId) {
        Map<String, Integer> crating = new HashMap<>();
        List<UserRating> ratings = userRatingsRepo.findAllByUserId(userId, Sort.unsorted()).stream()
                .filter(c -> c.getRating() > 0).collect(Collectors.toList());
        if (!ratings.isEmpty()) {

            int sum = ratings.stream().map(UserRating::getRating).reduce(0, Integer::sum);
            int rating = sum / ratings.size();
            crating.put("count", ratings.size());
            crating.put("rating", rating);
            return crating;
        }
        crating.put("count", 0);
        crating.put("rating", 0);
        return crating;

    }

    public List<UserRating> addUserReview(UserRating r) {
        r.setRating(0);
        userRatingsRepo.save(r);
        return userRatingsRepo.findAllByUserId(r.getUserId(), Sort.by("timestamp").descending()).stream()
                .filter(c -> c.getRating() == 0).collect(Collectors.toList());

    }

    public Map<String, Integer> addUserRatings(UserRating r) {
        if (r.getRating() > 0 && r.getRating() < 6)
            userRatingsRepo.save(r);
        return getUserRating(r.getUserId());
    }

    public List<UserRating> getUserReview(String userid) {
        return userRatingsRepo.findAllByUserId(userid, Sort.by("timestamp").descending()).stream()
                .filter(c -> c.getRating() == 0).collect(Collectors.toList());

    }

    public ResponseEntity<SignUpResponse> updateUser(Users users) {
        SignUpResponse signUpResponse = new SignUpResponse();
        Users user = userRepository.findOneByPhone(users.getPhone());
        if (user != null) {
            Positions positions = new Positions();
            if (user.getPosition().length >= 2) {
                positions.setLatitude(users.getPosition()[0]);
                positions.setLongitude(users.getPosition()[1]);
                positions.setLocationName(users.getLocationName());
                users.setPositions(positions);
            } else {
                users.setPositions(user.getPositions());
            }
            users.setRoles(user.getRoles());
            users.setRating(user.getRating());
            users.setPassword(user.getPassword());
            signUpResponse.setMessage("successfully updated");
            signUpResponse.setUsers(userRepository.save(users));
            return ResponseEntity.ok(signUpResponse);
        }
        signUpResponse.setMessage("Failed update, User not found");
        return ResponseEntity.badRequest().body(signUpResponse);
    }

    public ResponseEntity<?> getUsers(Principal principal) {
        SignInResponse signInResponse = new SignInResponse();
        String phone = principal.getName();
        Optional<Users> users = Optional.ofNullable(userRepository.findOneByPhone(phone));
        if (users.isPresent()) {
            Users u = users.get();
            u.setRoles(userRolesRepo.findAllByUserId(u.getPhone()).stream().map(UserRoles::getRole)
                    .collect(Collectors.toList()));
            signInResponse.setUsers(u);
            signInResponse.setMessage("successful login");
            return ResponseEntity.ok().body(signInResponse);
        } else {
            signInResponse.setMessage("User does not exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signInResponse);
        }
    }

    @Async
    public void sendMessage(@NonNull String phone) {
            String message = "Your account has been successfully created for the following phone number " + phone;
            messageSender.sendMessage(phone, message);
    }


    public ResponseEntity<?> getProviderById(String providerId) {

        Optional<Users> user = userRepository.findById(providerId);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get());

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provider not found");

    }

    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .filter(users -> users.getAccountType().equals("USER")).collect(Collectors.toList()));
    }
}
