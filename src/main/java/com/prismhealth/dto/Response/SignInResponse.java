package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prismhealth.Models.Users;

import lombok.Data;

@Data
public class SignInResponse {
    @JsonProperty("user")
    private Users users;
    @JsonProperty("message")
    private String message;

}
