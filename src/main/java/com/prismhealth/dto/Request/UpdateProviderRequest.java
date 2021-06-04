package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UpdateProviderRequest {
    @JsonProperty("oldPhone")
    private String oldPhone;
    @JsonProperty("newPhone")
    private String newPhone;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String secondName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("position")
    private double [] position;
}
