package com.prismhealth.dto.Request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prismhealth.Models.Positions;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

@Data
@JsonIgnoreProperties
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @JsonProperty
	private String password;
	@JsonProperty
	private String roles;
	@JsonProperty
	private String phone;
	@JsonProperty
	private String firstName;
	@JsonProperty
	private String secondName;
	@JsonProperty
	private String email;
	@JsonProperty
	private Date DateOfBirth;
	@JsonProperty
	private String gender;
}