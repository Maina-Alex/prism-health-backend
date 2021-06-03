package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties
@AllArgsConstructor
@NoArgsConstructor
public class Positions {
    @JsonProperty
    private double latitude;
    @JsonProperty
    private double longitude;
    @JsonProperty
    private String locationName;
    @JsonProperty
    private String radius;
}
