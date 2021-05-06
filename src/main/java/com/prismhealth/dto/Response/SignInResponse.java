package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prismhealth.Models.User;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class SignInResponse {
    @JsonProperty("user")
    private User user;
    @JsonProperty("message")
    private String message;

}
