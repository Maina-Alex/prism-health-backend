package com.prismhealth.dto.Request;

import java.util.Date;

import com.prismhealth.Models.Positions;
import lombok.Data;

import org.springframework.data.annotation.Transient;

@Data
public class SignUpRequest {

	private String password;

	private String roles;

	private String phone;

	private String firstName;

	private String secondName;

	private String email;

	private Date DateOfBirth;

	private String gender;

	private String location;

	private String latitude;

	private String longitude;

	@Transient
	public Positions getPositions() {
		Positions positions = new Positions();

		Double lat = 0.0;
		Double lDouble = 0.0;
		try {
			lat = Double.valueOf(latitude);
			lDouble = Double.valueOf(longitude);

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		positions.setLatitude(lat);
		positions.setLongitude(lDouble);

		positions.setLocationName(location);
		return positions;
	}

	@Override
	public String toString() {
		return String.format(
				"SignUpRequest{password = '%s',role = '%s',phone = '%s',"
						+ "name = '%s',email = '%s',gender = '%s',Dob = '%s'}",
				password, roles, phone, firstName + " " + secondName, email, gender, DateOfBirth);
	}
}