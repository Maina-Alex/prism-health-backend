package com.prismhealth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "uwazii")
public class UwaziiConfig {
    String senderId;
    String smsEndpointUrl;
    String Api_Key;
    String ClientId;
}
