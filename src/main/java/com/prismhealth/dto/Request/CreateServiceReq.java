package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prismhealth.Models.Positions;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties
public class CreateServiceReq {
    @NonNull
    @JsonProperty("name")
    private String name;
    @NonNull
    @JsonProperty("description")
    private String description;
    @NonNull
    @JsonProperty("position")
    private double[] position;
    @NonNull
    @JsonProperty("charges")
    private int charges;
    @NonNull
    @JsonProperty("subCategory")
    private String subCategory;
    @NonNull
    @JsonProperty("photos")
    private List<String> images;
    @JsonProperty("providerPhone")
    private String providerPhone;
}
