package com.prismhealth.Models;

import java.util.Date;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Bookings {
    @Id
    private String id;
    private String serviceId;
    private String userId;
    private Date date;
    private int hour;
    private boolean cancelled;
    private Date timestamp;

}
