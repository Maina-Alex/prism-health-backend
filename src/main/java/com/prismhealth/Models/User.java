package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class User{

	@JsonProperty("Id")
	private String Id;

	@JsonProperty("password")
	private String password;

	@JsonProperty("role")
	private String role;

	@Id
	@JsonProperty("phone")
	private String phone;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("secondName")
	private String secondName;

	@JsonProperty("emergencyContact1")
	private String emergencyContact1;

	@JsonProperty("emergencyContact2")
	private String emergencyContact2;

	@JsonProperty("email")
	private String email;

	@Override
	public String toString() {
		return String.format("User{password='%s', role='%s', phone='%s', firstName='%s', secondName='%s', email='%s'}", password, role, phone, firstName, secondName, email);
	}
}