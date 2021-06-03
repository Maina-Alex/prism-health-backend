package com.prismhealth.Models;

import lombok.Data;

@Data
public class Post {
    private int id;
    private String postTitle;
    private String postCategoryId;
    private String postContent;
    private String imageUrl;

    public Post(Integer id, String postTitle, String postCategoryId, String postContent, String imageUrl) {
        this.id = id;
        this.postTitle = postTitle;
        this.postCategoryId = postCategoryId;
        this.postContent = postContent;
        this.imageUrl = imageUrl;
    }
}
