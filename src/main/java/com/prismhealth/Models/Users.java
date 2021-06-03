package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Users {

	@Id
	private String id;
    @JsonProperty("phone")
	private String phone;
	@JsonProperty("password")
	private String password;
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
	@JsonProperty("profileImage")
	private String profileImage;
	@JsonProperty("dateOfBirth")
	private Date dateOfBirth;
	@JsonProperty("gender")
	private String gender;
	@JsonProperty("gender")
	private String auth;
	@JsonProperty("locationName")
	private String locationName;
	@JsonProperty("position")
	private double[] position;

	private Positions positions;
	@JsonProperty("username")
	private String username;

	private String accountType;

	private boolean verified;

	private String verificationToken;

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

	private Notifications notifications;

	private ProviderRating providerRating;
}