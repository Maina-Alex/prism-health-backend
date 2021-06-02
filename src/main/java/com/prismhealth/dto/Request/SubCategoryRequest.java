package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties
@AllArgsConstructor
public class SubCategoryRequest {
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("subCategory")
    private String subCategoryName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("photos")
    private String photos;
}
