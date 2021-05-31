package com.prismhealth.services;

import com.prismhealth.Models.Positions;
import com.prismhealth.Models.Users;
import com.prismhealth.config.UwaziiConfig;
import com.prismhealth.dto.Request.UwaziiSmsRequest;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.util.HelperUtility;
import okhttp3.*;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
public class SosService {
    private final AccountRepository accountRepository;
    private final UwaziiConfig uwaziiConfig;

    public SosService(AccountRepository accountRepository, UwaziiConfig uwaziiConfig) {
        this.accountRepository = accountRepository;
        this.uwaziiConfig = uwaziiConfig;
    }

    public ResponseEntity<String> sendSos(Positions position, Principal principal) {
        Users users = accountRepository.findOneByPhone(principal.getName());
        try {
        if (users.getEmergencyContact1()!=null&&users.getEmergencyContact2()==null){
            return execute(position,users,users.getEmergencyContact1()).get();
        }else if (users.getEmergencyContact1()!=null&&users.getEmergencyContact2()!=null){
            execute(position,users,users.getEmergencyContact1());
            return  execute(position,users,users.getEmergencyContact2()).get();
        }else {
            return execute(position,users,users.getEmergencyContact2()).get();
        }} catch (ExecutionException e) {
            e.printStackTrace();
        }return  null;
    }
    @Async
    public AsyncResult<ResponseEntity<String>> execute(Positions position, Users users, String phone){
        UwaziiSmsRequest uwaziiSmsRequest = new UwaziiSmsRequest();
        uwaziiSmsRequest.setApiKey(uwaziiConfig.getApi_Key());
        uwaziiSmsRequest.setSenderId(uwaziiConfig.getSenderId());
        uwaziiSmsRequest.setMessage(HelperUtility.getSosTemplate(users.getFirstName() + " " + users.getSecondName(),
                new Point(position.getLongitude(), position.getLatitude()), position.getLocationName()));
        if (phone.contains("+"))
            uwaziiSmsRequest.setMobileNumbers(phone.substring(1));
        else {
            String phoneNumber = "254" + phone.substring(1);
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
                return new AsyncResult<ResponseEntity<String>>( new ResponseEntity<String>("successfully sent", HttpStatus.ACCEPTED));
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(this.getClass())
                    .error(String.format("Could not send sms -> %s", e.getLocalizedMessage()));
            return null;
        }
        return null;
    }
}
