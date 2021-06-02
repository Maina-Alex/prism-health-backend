package com.prismhealth.Models;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
public class Post {
    private int id;
    private String postTitle;
    private String postCategoryId;
    private String postContent;
    private String imageUrl;

    public Post(Integer id ,String postTitle, String postCategoryId, String postContent, String imageUrl) {
        this.id=id;
        this.postTitle = postTitle;
        this.postCategoryId = postCategoryId;
        this.postContent = postContent;
        this.imageUrl = imageUrl;
    }
}
