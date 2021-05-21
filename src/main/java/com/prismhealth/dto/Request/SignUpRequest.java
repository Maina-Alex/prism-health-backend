package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prismhealth.Models.Positions;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.List;

@Data
public class SignUpRequest {


	@JsonProperty("password")
	private String password;

	@JsonProperty("roles")
	private String roles;

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

	@JsonProperty("location")
	private String location;

	@JsonProperty("latitude")
	private String latitude;

	@JsonProperty("longitude")
	private String longitude;
	 @Transient
	 public Positions getPositions(){
	 	Positions positions = new Positions();
	 	positions.setLatitude(Double.parseDouble(latitude));
	 	positions.setLongitude(Double.parseDouble(longitude));
	 	positions.setLocationName(location);
	 	return positions;
	 }

	@Override
 	public String toString(){
		return
				String.format("SignUpRequest{password = '%s',role = '%s',phone = '%s'," +
						"name = '%s',email = '%s',gender = '%s',Dob = '%s'}", password, roles, phone,
						firstName+" "+secondName, email, gender,DateOfBirth);
		}
}