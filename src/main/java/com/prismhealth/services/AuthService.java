package com.prismhealth.services;

import com.auth0.jwt.JWT;
import com.prismhealth.Models.AccountDetails;
import com.prismhealth.Models.Notification;
import com.prismhealth.Models.User;
import com.prismhealth.config.UwaziiConfig;
import com.prismhealth.dto.Request.UwaziiSmsRequest;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.NotificationRepo;
import com.prismhealth.repository.UserRolesRepo;
import com.prismhealth.security.SecurityConstants;
import com.prismhealth.util.Actions;
import com.prismhealth.util.AppConstants;
import com.prismhealth.util.HelperUtility;
import com.prismhealth.util.LogMessage;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AccountRepository usersRepo;


    private final BCryptPasswordEncoder encoder;
    private final RestTemplate restTemplate;
    private final NotificationRepo notificationRepo;
    private final UwaziiConfig uwaziiConfig;
    public AuthService(AccountRepository usersRepo, BCryptPasswordEncoder encoder,
                       RestTemplate restTemplate, NotificationRepo notificationRepo, UwaziiConfig uwaziiConfig){
        this.usersRepo = usersRepo;
        this.encoder = encoder;
        this.restTemplate = restTemplate;
        this.notificationRepo = notificationRepo;
        this.uwaziiConfig = uwaziiConfig;
    }

    public  String getAuthentication(String phone) {
        String confirmCode = HelperUtility.getConfirmCodeNumber();
        UwaziiSmsRequest uwaziiSmsRequest = new UwaziiSmsRequest();
        uwaziiSmsRequest.setApiKey(uwaziiConfig.getApi_Key());
        uwaziiSmsRequest.setSenderId(uwaziiConfig.getSenderId());
        uwaziiSmsRequest.setMessage(HelperUtility.getMessageTemplate(confirmCode));
        if (phone.contains("+"))
        uwaziiSmsRequest.setMobileNumbers(phone.substring(1));
        else {
           String phoneNumber = "254"+phone.substring(1);
            log.info("phone No-> "+phoneNumber);
            uwaziiSmsRequest.setMobileNumbers(phoneNumber);
        }
        uwaziiSmsRequest.setClientId(uwaziiConfig.getClientId());

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, Objects.requireNonNull(HelperUtility.toJson(uwaziiSmsRequest)));

        Request request = new Request.Builder()
                .url(uwaziiConfig.getSmsEndpointUrl())
                .post(body)
                .addHeader("content-type", "application/json")
                .build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.code()==200){
            log.info("Uwazii sms sent successfully ..");
            response.close();
            return confirmCode;
            }
        } catch (IOException e) {
            log.error(String.format("Could not send sms -> %s",e.getLocalizedMessage()));
            return null;
        }
        return null;
    }

    public String getToken(String phone) {
        Optional<User> users = Optional.ofNullable(usersRepo.findOneByPhone(phone));
        if (users.isPresent() && checkUserValidity(users.get())) {
            String token = JWT.create().withSubject(users.get().getPhone())
                    .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                    .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
            log.info("Getting token for id " + phone + " is " + LogMessage.SUCCESS);
            return token;
        } else {
            log.info("Getting token for firebase id " + phone + "  " + LogMessage.FAILED);
            return null;
        }

    }

    public String forgotPassword(String phone) {
        Optional<User> users = Optional.ofNullable(usersRepo.findOneByPhone(phone));

        if (users.isPresent()) {
            log.info("Forgot password request, user email  " + users.get().getEmail());
            String token = JWT.create().withSubject(users.get().getPhone())
                    .withExpiresAt(
                            new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME))
                    .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
            AccountDetails details = new AccountDetails();
            details.setAccesstoken(token);
            details.setEmail(users.get().getEmail());
            details.setUsername(users.get().getPhone());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            HttpEntity<AccountDetails> entity = new HttpEntity<>(details, headers);
            ResponseEntity<String> responseEntity = restTemplate
                    .postForEntity(AppConstants.notificationUrl + "/account/reset", entity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Notification notification = new Notification();
                notification.setEmail(users.get().getEmail());
                notification.setUserId(users.get().getPhone());
                notification.setMessage("Password reset request");
                notification.setAction(Actions.RESET_PASSSWORD);
                notification.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                notificationRepo.save(notification);
                log.info("Sent Password reset token to : " + users.get().getEmail() + " " + LogMessage.SUCCESS);
                return "Password reset sent to : " + users.get().getEmail();
            } else {
                log.info("Sending password reset to " + users.get().getEmail() + " " + LogMessage.FAILED);
                return null;

            }

        } else {
            log.info("Sending password reset to " + users.get().getEmail() + " " + LogMessage.FAILED + " User does not exist");
            return null;
        }

    }

    public User resetPassword(Principal principal, User users) {
        Optional<User> account = Optional.ofNullable(usersRepo.findOneByPhone(principal.getName()));
        if (account.isPresent()) {

            User oldUser = account.get();
            oldUser.setPassword(encoder.encode(users.getPassword()));
            log.info("Password reset for User id:" + users.getPhone() + " " + LogMessage.SUCCESS);
            return usersRepo.save(oldUser);

        } else
            return null;
    }

    public boolean checkUserValidity(User user) {
        return !user.isBlocked() && !user.isApproveDelete();
    }
}
