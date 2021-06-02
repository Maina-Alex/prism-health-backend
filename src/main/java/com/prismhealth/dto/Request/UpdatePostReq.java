package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties
public class UpdatePostReq {
    @JsonProperty("postId")
    private int postId;
    @JsonProperty("categoryId")
    private String categoryId;
    @JsonProperty("title")
    private String postTitle;
    @JsonProperty("content")
    private String postContent;
    @JsonProperty("url")
    private String imageUrl;
}
