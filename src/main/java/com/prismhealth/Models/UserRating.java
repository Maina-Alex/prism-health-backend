package com.prismhealth.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
public class UserRating {
    private String id;
    private String userId;
    private String review;
    private int rating;
    private Date timestamp;
}
