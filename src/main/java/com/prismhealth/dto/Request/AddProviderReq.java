package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties
public class AddProviderReq {
    @NonNull
    @JsonProperty("phone")
    private String phone;
    @NonNull
    @JsonProperty("password")
    private String password;
    @NonNull
    @JsonProperty("firstName")
    private String firstName;
    @NonNull
    @JsonProperty("lastName")
    private String secondName;
    @NonNull
    @JsonProperty("email")
    private String email;
    @NonNull
    @JsonProperty("position")
    private double[] position;
}
