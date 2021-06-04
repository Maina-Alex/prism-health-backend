package com.prismhealth.services;

import com.auth0.jwt.JWT;
import com.prismhealth.Models.*;

import com.prismhealth.dto.Request.Phone;
import com.prismhealth.dto.Request.SignUpRequest;

import com.prismhealth.dto.Request.UpdateForgotPasswordReq;

import com.prismhealth.dto.Request.UserUpdateRequest;
import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.*;
import com.prismhealth.security.SecurityConstants;

import com.prismhealth.util.HelperUtility;
import com.prismhealth.util.PhoneTrim;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
    private final UserRolesRepo userRolesRepo;
    private final BCryptPasswordEncoder encoder;
    private final MessageSender messageSender;

    public ResponseEntity<SignUpResponse> authentication(Phone phone) {
        SignUpResponse signUpResponse = new SignUpResponse();

            String authCode = "ABCDE";
            // try {
            // authCode = authService.getAuthentication(phone.getPhone()).get();
            // } catch (InterruptedException | ExecutionException e) {
            // e.printStackTrace();
            // }
            signUpResponse.setMessage("get otp");
            signUpResponse.setAuthCode(authCode);
        
        return ResponseEntity.ok(signUpResponse);
    }

    public ResponseEntity<SignUpResponse> signUpUser(SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = new SignUpResponse();

        Users thisUsers = userRepository.findByPhone(signUpRequest.getPhone());
        if (thisUsers == null) {
            Users users1 = new Users();
            users1.setPassword(encoder.encode(signUpRequest.getPassword()));
            users1.setPhone(PhoneTrim.trim(signUpRequest.getPhone()));
            users1.setEmail(signUpRequest.getEmail());
            users1.setFirstName(signUpRequest.getFirstName());
            users1.setSecondName(signUpRequest.getSecondName());
            users1.setGender(signUpRequest.getGender());
            users1.setDateOfBirth(signUpRequest.getDateOfBirth());
            users1.setEmergencyContact1(null);
            users1.setEmergencyContact2(null);
            users1.setAccountType("USER");
            log.info("Registering new Mobile User:  Id:" + PhoneTrim.trim(users1.getPhone()));

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

    public ResponseEntity<?> updateForgotPassword(@NonNull UpdateForgotPasswordReq req) {
        Users user = userRepository.findByPhone(req.getPhone());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        user.setPassword(encoder.encode(req.getPassword()));
        user.setVerificationToken("");
        userRepository.save(user);
        return ResponseEntity.ok().body("User password successfully updated");
    }

    public ResponseEntity<?> forgotPassword(@NonNull Phone phone) {

        try {
            Users users = userRepository.findByPhone(PhoneTrim.trim(phone.getPhone()));
            if (users == null) {
                return new ResponseEntity<>("User with phone number " + phone + " not found", HttpStatus.NOT_FOUND);
            } else {
                log.info("Forgot password request, user email  " + users.getEmail());
                String code = Objects.requireNonNull(forgotPasswordMail(PhoneTrim.trim(phone.getPhone()))).get();
                if (code != null) {
                    users.setVerificationToken(code);
                    userRepository.save(users);
                    return ResponseEntity.ok().body(code);
                }
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("User password not modified");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("User password not modified");
        }
    }

    private Future<String> forgotPasswordMail(String phone) {
        String code = HelperUtility.getConfirmCodeNumber();
        return messageSender.sendMessage(PhoneTrim.trim(phone), code);
    }

    public String getToken(String phone) {
        Optional<Users> users = Optional.ofNullable(userRepository.findByPhone(PhoneTrim.trim(phone)));
        return users.map(value -> JWT.create().withSubject(value.getPhone())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()))).orElse(null);
    }

    public ResponseEntity<?> changePassword(PasswordReset reset) {
        return ResponseEntity.ok(authService.resetPassword(reset.getPassword(), reset.getAuthCode()));
    }

    public ResponseEntity<?> updateUser(UserUpdateRequest request, Principal principal) {

        Users user = userRepository.findByPhone(principal.getName());

        if (user != null) {
            try {

                if (Optional.ofNullable(request.getDateOfBirth()).isPresent())
                    user.setDateOfBirth(request.getDateOfBirth());
                if ( !request.getFirstName().equals(""))
                    user.setFirstName(request.getFirstName());
                if (!request.getSecondName().equals(""))
                    user.setSecondName(request.getSecondName());
                if (!request.getEmergencyContact1().equals(""))
                    user.setEmergencyContact1(request.getEmergencyContact1());
                if (!request.getEmergencyContact2().equals(""))
                    user.setEmergencyContact2(request.getEmergencyContact2());
                if (!request.getEmail().equals(""))
                    user.setEmail(request.getEmail());
                if (!request.getGender().equals(""))
                    user.setGender(request.getGender());
                Users saved = userRepository.save(user);

                return ResponseEntity.ok().body(saved);
            } catch (Exception ex) {
                ex.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
            }

        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    public ResponseEntity<?> getUsers(Principal principal) {
        SignInResponse signInResponse = new SignInResponse();
        String phone = principal.getName();
        Optional<Users> users = Optional.ofNullable(userRepository.findByPhone(PhoneTrim.trim(phone)));
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
        String message = "Your account has been successfully created for the following phone number " + PhoneTrim.trim(phone);
        messageSender.sendMessage(PhoneTrim.trim(phone), message);
    }

    public ResponseEntity<?> getProviderByPhone(String phone) {

        Users user = userRepository.findByPhone(PhoneTrim.trim(phone));
        if (user != null) {
            return ResponseEntity.ok().body(user);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provider not found");

    }

    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .filter(users -> users.getAccountType().equals("USER")).collect(Collectors.toList()));
    }
}
