package com.prismhealth.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document
public class Users {

	private String password;
	@Id

	private String phone;

	private String firstName;

	private String secondName;

	private String emergencyContact1;

	private String emergencyContact2;

	private String email;

	private String profileImage;

	private Date dateOfBirth;

	private String gender;

	private String auth;

	private double rating;

	private String locationName;

	private double[] position;

	private Positions positions;

	private String username;

	private String accountType;

	private boolean verified;

	private String deviceToken;

	private Date verifiedOn;

	private String verifiedBy;

	private List<String> roles;

	private boolean blocked;

	private Date blockedOn;

	private String blockedBy;

	private boolean deleted;

	private Date deletedOn;

	private String deletedBy;

	private String approveDeleteBy;

	private Date opproveDeleteOn;

	private boolean approveDelete;

	@Override
	public String toString() {
		return String.format(
				"User{password='%s', role='%s', phone='%s', firstName='%s', secondName='%s',"
						+ " email='%s',gender = '%s',Dob = '%s'}",
				password, roles, phone, firstName, secondName, email, gender);
	}
}