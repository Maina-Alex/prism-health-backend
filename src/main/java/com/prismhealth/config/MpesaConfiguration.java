package com.prismhealth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mpesa")
public class MpesaConfiguration {
    private String consumerKey;
    private String consumerSecret;
    private String businessShortCode;
    private String passKey;
    private String callBackUrl;
    private String StkPushUrl;
    private String initiator_security_credential;
    private String OAuthUrlEndpoint;
}
