package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonIgnoreProperties
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateForgotPasswordReq {
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("password")
    private String password;
}
