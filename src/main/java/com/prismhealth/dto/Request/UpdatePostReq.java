package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@JsonIgnoreProperties
@NoArgsConstructor
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
