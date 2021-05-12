package com.prismhealth.Models;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document

public class Issues {

    @Id
    private String id;
    private String status;
    private Date timestamp;
    private String email;
    private String phoneNumber;
    private String fullname;
    private String message;
    private String userId;
    private String closedBy;
    private Date closedOn;
    private List<Help> helpMessages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Help> getHelpMessages() {
        return helpMessages;
    }

    public void setHelpMessages(List<Help> helpMessages) {
        this.helpMessages = helpMessages;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }

    public Date getClosedOn() {
        return closedOn;
    }

    public void setClosedOn(Date closedOn) {
        this.closedOn = closedOn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
