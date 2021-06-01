package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostCategoryReq {
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("description")
    String description;
}
