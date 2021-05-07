package com.prismhealth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.dto.Request.UwaziiSmsRequest;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class HelperUtility {

    public static String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
            } catch (JsonProcessingException e) {
                return null;
            }
    }

    public static String getMessageTemplate(String code) {
        return String.format("Please Enter code below\n %s \n to confirm your account.",code);
    }
    public static String getConfirmCodeNumber(){
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0','z')
                .filteredBy(CharacterPredicates.LETTERS,CharacterPredicates.DIGITS)
                .build();
        return randomStringGenerator.generate(5).toUpperCase();
    }
    public static String getStkPushPassword(String shortCode,String passKey,String timeStamp){
        String concatedString = String.format("%s%s%s",shortCode,passKey,timeStamp);
        return toBase64String(concatedString);
    }
    public static String toBase64String(String value){
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(data);
    }
    public static String getTimeStamp(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(LocalDateTime.now());
    }
    public static String calculateAmount(String origin,String destination){
        //TODO  calculate the amount based on the distance between origin and destination
        int amount = 1;
        return String.valueOf(amount);
    }
}
