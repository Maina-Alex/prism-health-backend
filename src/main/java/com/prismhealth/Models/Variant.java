package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Variant {
    @JsonProperty("variantName")
    private String variantName;

    @JsonProperty("subCategory")
    private String subCategory;
}
