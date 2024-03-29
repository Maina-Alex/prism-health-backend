package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryRequest {
    @JsonProperty("type")
    private String categoryType;
    @JsonProperty("name")
    private String categoryName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("photo")
    private String photo;
}
