package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Variant {
    @JsonProperty("variantName")
    private String variantName;

    @JsonProperty("subCategory")
    private String subCategory;
}
