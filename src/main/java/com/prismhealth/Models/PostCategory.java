package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class PostCategory {
    @Id
    private String id;
    private String categoryName;
    String description;

    public PostCategory(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }
}
