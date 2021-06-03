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
public class PostCategoryReq {
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("description")
    String description;
}
