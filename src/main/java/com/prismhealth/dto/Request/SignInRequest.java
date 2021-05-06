package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SignInRequest {

	@JsonProperty("password")
	private String password;

	@JsonProperty("phone")
	private String phone;
}