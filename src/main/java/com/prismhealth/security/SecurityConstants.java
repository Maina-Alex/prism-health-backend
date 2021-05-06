package com.prismhealth.security;

public class SecurityConstants {
        public static final String SECRET = "777e3ba3-d157-427c-86b6-e3eb010edd62";
        public static final long EXPIRATION_TIME = 1_800_000 * 2 * 24;// 30 * 2 * 24 minutes
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String HEADER_STRING = "Authorization";

        public static final long PASSWORD_RESET_EXPIRATION_TIME = 900_000;// about 15 minutes

}
