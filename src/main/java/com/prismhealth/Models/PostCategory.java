package com.prismhealth.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class PostCategory {
    @Id
    private String id;
    private String categoryName;
    private String description;
    private List<Post> posts = new ArrayList<>();
    private int postCounter = 0;

    public PostCategory(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;

    }
}
