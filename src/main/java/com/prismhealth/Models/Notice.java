package com.prismhealth.Models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class Notice {
    private String id;
    private String userId;
    private String email;
    private String title;
    private AccountDetails details;
    private String action;
    private String message;
    private Date timestamp = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    private PushNotification pushNotification;

}
