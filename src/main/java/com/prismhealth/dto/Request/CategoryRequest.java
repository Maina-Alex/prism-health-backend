package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties
public class CategoryRequest {
    @JsonProperty("categoryType")
    private String categoryType;
    @JsonProperty("CategoryName")
    private String categoryName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("photo")
    private String photo;
}
