package com.prismhealth.services;

import com.auth0.jwt.JWT;

import com.prismhealth.Models.Users;
import com.prismhealth.config.UwaziiConfig;
import com.prismhealth.dto.Request.UwaziiSmsRequest;

import java.io.IOException;
import java.security.Principal;

import java.util.Date;

import java.util.Optional;

import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.MailService;
import com.prismhealth.repository.NotificationRepo;
import com.prismhealth.security.SecurityConstants;

import com.prismhealth.util.HelperUtility;
import com.prismhealth.util.LogMessage;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AccountRepository usersRepo;
    private final BCryptPasswordEncoder encoder;
    private final MailService mailService;
    private final NotificationRepo notificationRepo;
    private final UwaziiConfig uwaziiConfig;

    public AuthService(AccountRepository usersRepo, BCryptPasswordEncoder encoder, MailService mailService,
            NotificationRepo notificationRepo, UwaziiConfig uwaziiConfig) {
        this.usersRepo = usersRepo;
        this.encoder = encoder;
        this.mailService = mailService;
        this.notificationRepo = notificationRepo;
        this.uwaziiConfig = uwaziiConfig;
    }

    public String getAuthentication(String phone) {
        String confirmCode = HelperUtility.getConfirmCodeNumber();
        UwaziiSmsRequest uwaziiSmsRequest = new UwaziiSmsRequest();
        uwaziiSmsRequest.setApiKey(uwaziiConfig.getApi_Key());
        uwaziiSmsRequest.setSenderId(uwaziiConfig.getSenderId());
        uwaziiSmsRequest.setMessage(HelperUtility.getMessageTemplate(confirmCode));
        if (phone.contains("+"))
            uwaziiSmsRequest.setMobileNumbers(phone.substring(1));
        else {
            String phoneNumber = "254" + phone.substring(1);
            log.info("phone No-> " + phoneNumber);
            uwaziiSmsRequest.setMobileNumbers(phoneNumber);
        }
        uwaziiSmsRequest.setClientId(uwaziiConfig.getClientId());

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(HelperUtility.toJson(uwaziiSmsRequest), mediaType);

        Request request = new Request.Builder().url(uwaziiConfig.getSmsEndpointUrl()).post(body)
                .addHeader("content-type", "application/json").build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.code() == 200) {
                log.info("Uwazii sms sent successfully ..");
                response.close();
                return confirmCode;
            }
        } catch (IOException e) {
            log.error(String.format("Could not send sms -> %s", e.getLocalizedMessage()));
            return null;
        }
        return null;
    }

    public String getToken(String phone) {
        Optional<Users> users = Optional.ofNullable(usersRepo.findOneByPhone(phone));
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

    public Users resetPassword(Principal principal, Users users) {
        Optional<Users> account = Optional.ofNullable(usersRepo.findOneByPhone(principal.getName()));
        if (account.isPresent()) {

            Users oldUsers = account.get();
            oldUsers.setPassword(encoder.encode(users.getPassword()));
            log.info("Password reset for User id:" + users.getPhone() + " " + LogMessage.SUCCESS);
            return usersRepo.save(oldUsers);

        } else
            return null;
    }

    public boolean checkUserValidity(Users users) {
        return !users.isBlocked() && !users.isApproveDelete();
    }
}
