package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prismhealth.Models.User;
import lombok.Data;

@Data
public class SignUpResponse {
    @JsonProperty("user")
    private User user;
    @JsonProperty("message")
    private String message;
}
