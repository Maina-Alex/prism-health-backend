package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PostCategory {

    @JsonProperty("categoryName")
    private String categoryName;
    @Id
    private String id;

    @JsonProperty("description")
    String description;


}
