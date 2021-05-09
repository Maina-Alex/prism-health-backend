package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class User{

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

	@JsonProperty("DateOfBirth")
	private String DateOfBirth;

	@JsonProperty("gender")
	private String gender;

	@Override
	public String toString() {
		return String.format("User{password='%s', role='%s', phone='%s', firstName='%s', secondName='%s', email='%s',gender = '%s',Dob = '%s'}", password, role, phone, firstName, secondName, email, gender,DateOfBirth);
	}
}