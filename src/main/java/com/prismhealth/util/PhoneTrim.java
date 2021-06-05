package com.prismhealth.util;

public class PhoneTrim {
    //Applies to Kenyan phones only
    public static String trim(String phone){
        if(phone.startsWith("0")) {
            return "+254"+phone.substring(1);
        }
        return phone;
    }
}
