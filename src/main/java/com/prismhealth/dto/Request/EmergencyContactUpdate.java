package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyContactUpdate {
    @JsonProperty
    private String phone;
    @JsonProperty
    private String emergencyContact1;
    @JsonProperty
    private String emergencyContact2;
}
