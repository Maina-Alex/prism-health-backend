package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("title")
    private String postTitle;
    @JsonProperty("content")
    private String postContent;
    @JsonProperty("url")
    private String imageUrl;
}
