package com.prismhealth.util;

import java.util.Random;

public class UtilityFunctions {
    public static String getRandomString() {
        String randomString = "";
        final String alphabet = "0123456789ABCDE";
        final int N = alphabet.length();

        Random r = new Random();

        for (int i = 0; i < 16; i++) {
            randomString += String.valueOf(alphabet.charAt(r.nextInt(N)));
        }

        return randomString;
    }
}
