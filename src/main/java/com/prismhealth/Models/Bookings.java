package com.prismhealth.Models;

import java.util.Date;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class Bookings {
    private String id;
    private String serviceId;
    private String userId;
    private String date;
    private int hour;
    private boolean cancelled;
    private Date timestamp;
    private Users user;
    private Services service;

}
