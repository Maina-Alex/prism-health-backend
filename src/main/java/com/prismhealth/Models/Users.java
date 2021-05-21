package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;
import java.util.List;

@Data
public class Users {

	@JsonProperty("password")
	private String password;
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

	@JsonProperty("auth")
	private String auth;


	@JsonProperty("rating")
	private double rating;
	@JsonProperty("locationName")
	private String locationName;
	@JsonProperty("position")
	private double[] position;
	@JsonProperty("positions")
	private Positions positions;
	@JsonProperty("username")
	private String username;
	@JsonProperty("accountType")
	private String accountType;
	@JsonProperty("verified")
	private boolean verified;
	@JsonProperty("deviceToken")
	private String deviceToken;
	@JsonProperty("verifiedOn")
	private Date verifiedOn;
	@JsonProperty("verifiedBy")
	private String verifiedBy;
	@JsonProperty("roles")
	private List<String> roles;
	@JsonProperty("blocked")
	private boolean blocked;
	@JsonProperty("blockedOn")
	private Date blockedOn;
	@JsonProperty("blockedBy")
	private String blockedBy;
	@JsonProperty("deleted")
	private boolean deleted;
	@JsonProperty("deletedOn")
	private Date deletedOn;
	@JsonProperty("deletedBy")
	private String deletedBy;
	@JsonProperty("approveDeleteBy")
	private String approveDeleteBy;
	@JsonProperty("opproveDeleteOn")
	private Date opproveDeleteOn;
	@JsonProperty("approveDelete")
	private boolean approveDelete;

	@Override
	public String toString() {
		return String.format("User{password='%s', role='%s', phone='%s', firstName='%s', secondName='%s'," +
				" email='%s',gender = '%s',Dob = '%s'}", password, roles, phone, firstName, secondName, email, gender,DateOfBirth);
	}
}