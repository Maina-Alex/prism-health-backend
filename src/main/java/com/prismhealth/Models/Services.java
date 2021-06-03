package com.prismhealth.Models;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class Services {
    @Id
    private String id;
    private String name;
    private String description;
    private double[] position;
    private int charges;
    private boolean verified;
    private String subCategory;
    private boolean available;
    private List<String> images;
    private String providerPhone;
    private Date approvedOn;
    private Date timestamp;

}
