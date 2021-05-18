package com.prismhealth.Models;

import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document
public class Services {
    @Id
    private String id;
    private String name;
    private String description;
    private double[] position;
    private int charges;
    private double rating;
    private long ratingsCount;
    private boolean verified;
    private String subCategory;
    private String tag;
    private boolean available;
    private String type;
    private String images;
    private List<ServiceBooking> bookings;
    private String providerId;
    private String locationName;

    private String approvedBy;
    private Date approvedOn;

    private Date timestamp;

}
