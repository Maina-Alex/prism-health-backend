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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getEmergencyContact1() {
		return emergencyContact1;
	}

	public void setEmergencyContact1(String emergencyContact1) {
		this.emergencyContact1 = emergencyContact1;
	}

	public String getEmergencyContact2() {
		return emergencyContact2;
	}

	public void setEmergencyContact2(String emergencyContact2) {
		this.emergencyContact2 = emergencyContact2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	public Positions getPositions() {
		return positions;
	}

	public void setPositions(Positions positions) {
		this.positions = positions;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public Date getVerifiedOn() {
		return verifiedOn;
	}

	public void setVerifiedOn(Date verifiedOn) {
		this.verifiedOn = verifiedOn;
	}

	public String getVerifiedBy() {
		return verifiedBy;
	}

	public void setVerifiedBy(String verifiedBy) {
		this.verifiedBy = verifiedBy;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public Date getBlockedOn() {
		return blockedOn;
	}

	public void setBlockedOn(Date blockedOn) {
		this.blockedOn = blockedOn;
	}

	public String getBlockedBy() {
		return blockedBy;
	}

	public void setBlockedBy(String blockedBy) {
		this.blockedBy = blockedBy;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public String getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}

	public String getApproveDeleteBy() {
		return approveDeleteBy;
	}

	public void setApproveDeleteBy(String approveDeleteBy) {
		this.approveDeleteBy = approveDeleteBy;
	}

	public Date getOpproveDeleteOn() {
		return opproveDeleteOn;
	}

	public void setOpproveDeleteOn(Date opproveDeleteOn) {
		this.opproveDeleteOn = opproveDeleteOn;
	}

	public boolean isApproveDelete() {
		return approveDelete;
	}

	public void setApproveDelete(boolean approveDelete) {
		this.approveDelete = approveDelete;
	}
}