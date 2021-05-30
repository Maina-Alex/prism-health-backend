package com.prismhealth.Models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class Notification {

    @Id
    private String id;
    private String userId;
    private String email;
    private String title;
    private AccountDetails details;
    private String action;
    private String message;
    private Date timestamp;
    private PushNotification pushNotification;

}
