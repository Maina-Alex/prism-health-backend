package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prismhealth.Models.Users;
import lombok.Data;

@Data
public class SignUpResponse {
    @JsonProperty("user")
    private Users users;
    @JsonProperty("authCode")
    private String authCode;
    @JsonProperty("message")
    private String message;
}
