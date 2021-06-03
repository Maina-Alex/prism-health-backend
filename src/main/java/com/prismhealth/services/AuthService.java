package com.prismhealth.services;

import com.prismhealth.Models.Users;
import com.prismhealth.config.UwaziiConfig;
import com.prismhealth.dto.Request.UwaziiSmsRequest;

import java.io.IOException;

import java.util.Objects;
import java.util.concurrent.Future;

import com.prismhealth.repository.UserRepository;
import com.prismhealth.repository.MailService;


import com.prismhealth.util.HelperUtility;
import com.prismhealth.util.LogMessage;

import lombok.AllArgsConstructor;
import okhttp3.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
@AllArgsConstructor
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository usersRepo;
    private final BCryptPasswordEncoder encoder;
    private final MailService mailService;
    private final UwaziiConfig uwaziiConfig;

    @Async
    public Future<String> getAuthentication(String phone) {
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
        RequestBody body = RequestBody.create(mediaType,
                Objects.requireNonNull(HelperUtility.toJson(uwaziiSmsRequest)));

        Request request = new Request.Builder().url(uwaziiConfig.getSmsEndpointUrl()).post(body)
                .addHeader("content-type", "application/json").build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.code() == 200) {
                log.info("Uwazii sms sent successfully ..");
                response.close();
                return new AsyncResult<>(confirmCode);
            }
        } catch (IOException e) {
            log.error(String.format("Could not send sms -> %s", e.getLocalizedMessage()));
            return null;
        }
        return null;
    }

    public Users resetPassword(String password, String phone) {
        Users accounts = usersRepo.findByPhone(phone);
        if (accounts!=null) {
            accounts.setPassword(encoder.encode(password));
            log.info("Password reset for User id:" + accounts.getPhone() + " " + LogMessage.SUCCESS);
            return usersRepo.save(accounts);

        } else
            return null;
    }

    public boolean checkUserValidity(Users users) {
        return !users.isBlocked() && !users.isApproveDelete();
    }
}
