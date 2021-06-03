package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@JsonIgnoreProperties
@AllArgsConstructor
@NoArgsConstructor
@Data

public class UpdateForgotPasswordReq {
    @JsonProperty("phone")
    @NonNull
    private String phone;
    @JsonProperty("password")
    @NonNull
    private String password;
}
