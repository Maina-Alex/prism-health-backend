package com.prismhealth.services;

import com.prismhealth.config.UwaziiConfig;
import com.prismhealth.dto.Request.UwaziiSmsRequest;
import com.prismhealth.util.HelperUtility;
import lombok.AllArgsConstructor;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
public class MessageSender {
    private final UwaziiConfig uwaziiConfig;
    private final Logger log = LoggerFactory.getLogger(MessageSender.class);

    public  Future<String> sendMessage( String phone, String message){
        UwaziiSmsRequest uwaziiSmsRequest = new UwaziiSmsRequest();
        uwaziiSmsRequest.setApiKey(uwaziiConfig.getApi_Key());
        uwaziiSmsRequest.setSenderId(uwaziiConfig.getSenderId());
        uwaziiSmsRequest.setMessage(message);
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
                response.close();
                return new AsyncResult<>(message);
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
