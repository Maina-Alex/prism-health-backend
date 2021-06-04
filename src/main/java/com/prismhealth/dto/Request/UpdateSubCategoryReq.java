package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSubCategoryReq {
    @JsonProperty("categoryName")
    @JsonIgnore
    private String categoryName;
    @JsonProperty("oldName")
    @JsonIgnore
    private String oldName;
    @JsonIgnore
    @JsonProperty("subCategoryName")
    private String subCategoryName;
    @JsonIgnore
    @JsonProperty("description")
    private String description;
    @JsonIgnore
    @JsonProperty("photo")
    private String photo;
}
