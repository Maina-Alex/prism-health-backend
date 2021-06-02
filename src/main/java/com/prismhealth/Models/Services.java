package com.prismhealth.Models;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private Positions positions;
    private int charges;
    private double rating;
    private long ratingsCount;
    private boolean verified;
    private String subCategory;
    private String tag;
    private boolean available;
    private String type;
    private List<String> images;
    private String providerId;
    private String approvedBy;
    private Date approvedOn;
    private Date timestamp;

}
