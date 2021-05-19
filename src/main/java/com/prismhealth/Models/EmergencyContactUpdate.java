package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmergencyContactUpdate {
    @JsonProperty
    private String phone;
    @JsonProperty
    private String emergencyContact1;
    @JsonProperty
    private String emergencyContact2;
}
