package com.prismhealth.Models;

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

	private String phone;

	private String password;

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

	private List<Bookings> bookings;

	private List<Notification> notifications;
}