package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class SignUpRequest {


	@JsonProperty("password")
	private String password;

	@JsonProperty("role")
	private String role;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("secondName")
	private String secondName;

	@JsonProperty("email")
	private String email;

	@JsonProperty("DateOfBirth")
	private String DateOfBirth;

	@JsonProperty("gender")
	private String gender;

	@Override
 	public String toString(){
		return
				String.format("SignUpRequest{password = '%s',role = '%s',phone = '%s'," +
						"name = '%s',email = '%s',gender = '%s',Dob = '%s'}", password, role, phone,
						firstName+" "+secondName, email, gender,DateOfBirth);
		}
}