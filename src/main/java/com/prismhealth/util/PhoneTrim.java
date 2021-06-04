package com.prismhealth.util;

public class PhoneTrim {
    //Applies to Kenyan phones only
    public static String trim(String phone){
        if(phone.startsWith("0")) {
            return phone.replace("0", "+254");
        }
        return phone;
    }
}
