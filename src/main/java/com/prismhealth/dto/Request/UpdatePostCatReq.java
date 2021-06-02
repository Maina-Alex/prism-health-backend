package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties
@Data
public class UpdatePostCatReq {
    @JsonProperty("categoryId")
    private String categoryId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;

}
