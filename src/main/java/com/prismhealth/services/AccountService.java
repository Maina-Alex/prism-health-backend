package com.prismhealth.services;

import com.auth0.jwt.JWT;
import com.prismhealth.Models.*;

import com.prismhealth.dto.Request.Phone;
import com.prismhealth.dto.Request.SignUpRequest;

import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.*;
import com.prismhealth.security.SecurityConstants;
import com.prismhealth.util.Actions;

import com.prismhealth.util.HelperUtility;
import com.prismhealth.util.LogMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice.Return;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AuthService authService;
    private final UserRatingsRepo userRatingsRepo;
    private final UserRolesRepo userRolesRepo;
    private final BCryptPasswordEncoder encoder;
    private final  NotificationRepo notificationRepo;
    private final MailService mailService;
    private final ExecutorService executor;


    public ResponseEntity<SignUpResponse> authentication(Phone phone) {
        SignUpResponse signUpResponse = new SignUpResponse();
        Users users = accountRepository.findOneByPhone(phone.getPhone());
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

        Users thisUsers = accountRepository.findOneByPhone(signUpRequest.getPhone());
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

            users1 = accountRepository.save(users1);
            signUpResponse.setMessage("successfully created");

            UserRoles role = new UserRoles();
            role.setAssignedBy("DEFAULT");
            role.setRole("ROLE_USER");// "ROLE_ADMIN", "ROLE_HELP_SUPPORT", "ROLE_SITE_CONTENT_UPDATER"));
            role.setUserId(users1.getPhone());
            userRolesRepo.save(role);
            log.info("Assigned Default User Role to UserId:" + users1.getPhone());

            sendEmail(users1, "createAccount");

            signUpResponse.setUsers(users1);
            return ResponseEntity.ok().body(signUpResponse);
        } else {

            signUpResponse.setMessage("User already exists sign in..");
            return ResponseEntity.badRequest().body(signUpResponse);
        }
    }

    public ResponseEntity<?> forgotPassword(Phone phone) {
        // TODO implement the notification service to send the change password link.
        log.info("Send link to email " + phone);
        Users users = accountRepository.findOneByPhone(phone.getPhone());
        if (users == null) {
            return new ResponseEntity<>("User with phone number " + phone + " not found", HttpStatus.NOT_FOUND);
        } else {

            log.info("Forgot password request, user email  " + users.getEmail());
            String authCode = HelperUtility.getConfirmCodeNumber();
            users.setDeviceToken(authCode);
            accountRepository.save(users);
            AccountDetails details = new AccountDetails();
            details.setAccesstoken(authCode);
            details.setEmail(users.getEmail());
            details.setUsername(users.getPhone());
            forgotPasswordMail(users,authCode,details);
            return new ResponseEntity<>("Ok", HttpStatus.OK);
        }
    }

    @Async
    private void forgotPasswordMail(Users users, String authCode,AccountDetails details){
        Mail mail = new Mail();
        mail.setMailFrom("prismhealth658@gmail.com");
        mail.setMailTo(users.getEmail());
        mail.setMailSubject("Prism-health Notification services");
        mail.setMailContent(
                "" + "Here is your authentication code \n" + authCode + "\nUse to change your password. ");

        mailService.sendEmail(mail);
        Notification notification = new Notification();
        notification.setEmail(users.getEmail());
        notification.setUserId(users.getPhone());
        notification.setDetails(details);

        notification.setMessage("Click on the link to change your password");
        notification.setAction(Actions.RESET_PASSSWORD);
        notification.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        notificationRepo.save(notification);
        log.info("Sent notification to : " + users.getEmail() + " " + LogMessage.SUCCESS);
    }

    public String getToken(String phone) {
        Optional<Users> users = Optional.ofNullable(accountRepository.findOneByPhone(phone));
        if (users.isPresent()) {
            String token = JWT.create().withSubject(users.get().getPhone())
                    .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                    .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
            log.info("Getting token for firebase id " + phone + " is " + LogMessage.SUCCESS);
            return token;
        } else {
            log.info("Getting token for firebase id " + phone + "  " + LogMessage.FAILED);
            return null;
        }

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
        Users user = accountRepository.findOneByPhone(users.getPhone());
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
            signUpResponse.setUsers(accountRepository.save(users));
            return ResponseEntity.ok(signUpResponse);
        }
        signUpResponse.setMessage("Failed update, User not found");
        return ResponseEntity.badRequest().body(signUpResponse);
    }

    public ResponseEntity<?> getUsers(Principal principal) {
        SignInResponse signInResponse = new SignInResponse();
        String phone = principal.getName();
        Optional<Users> users = Optional.ofNullable(accountRepository.findOneByPhone(phone));
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
    public void sendEmail(Users users, String action) {
            if (users == null) {
                log.info("User with phone number not found");
            }
            String message = null;
            if (action.equals("createAccount")) {
                message = "Account successfully created for " + users.getPhone();
            } else if (action.equals("createProduct")) {
                message = "Product successfully created by " + users.getPhone() + " " + users.getEmail();
            } else if (action.equals("createService")) {
                message = "Service successfully created by " + users.getPhone() + " " + users.getEmail();
            } else if (action.equals("createBooking")) {
                message = "Booking successfully created by " + users.getPhone() + " " + users.getEmail();
            } else if (action.equals("notifyProvider")) {
                message = "Product booking made for your product";
            }

            if (users != null) {
                log.info(message);
                Mail mail = new Mail();
                mail.setMailFrom("prismhealth658@gmail.com");
                mail.setMailTo(users.getEmail());
                mail.setMailSubject("Prism-health Notification services");
                mail.setMailContent(message);

                mailService.sendEmail(mail);
                Notification notification = new Notification();
                notification.setEmail(users.getEmail());
                notification.setUserId(users.getPhone());
                notification.setMessage(message);
                notification.setAction(Actions.RESET_PASSSWORD);
                notification.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                notificationRepo.save(notification);
                log.info("Sent notification to : " + users.getEmail() + " " + LogMessage.SUCCESS);

            } else {
                log.info("Sending notification  " + LogMessage.FAILED + " User does not exist");

            }

    }

    public ResponseEntity<?> getProviderById(String providerId) {

        Optional<Users> user = accountRepository.findById(providerId);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get());

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Provider not found");

    }

    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(accountRepository.findAll().stream()
                .filter(users -> users.getAccountType().equals("USER")).collect(Collectors.toList()));
    }
}
