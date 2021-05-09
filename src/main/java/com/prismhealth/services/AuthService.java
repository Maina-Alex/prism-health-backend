package com.prismhealth.services;

import com.prismhealth.Models.User;
import com.prismhealth.config.UwaziiConfig;
import com.prismhealth.dto.Request.SignUpRequest;
import com.prismhealth.dto.Request.UwaziiSmsRequest;

import java.io.IOException;
import java.util.Objects;

import com.prismhealth.util.HelperUtility;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UwaziiConfig uwaziiConfig;
    public AuthService(UwaziiConfig uwaziiConfig){
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
}
