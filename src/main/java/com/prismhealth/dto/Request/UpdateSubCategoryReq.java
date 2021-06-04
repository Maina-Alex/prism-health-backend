package com.prismhealth.dto.Request;

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
    private String categoryName;
    @JsonProperty("oldName")
    private String oldName;
    @JsonProperty("subCategoryName")
    private String subCategoryName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("photo")
    private String photo;
}
