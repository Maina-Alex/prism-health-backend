package com.prismhealth.Models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BlockedUser {
    @Id
    private String id;
    private User user;
    private String blockedBy;
    private Date blockedOn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
    }

    public Date getBlockedOn() {
        return blockedOn;
    }

    public void setBlockedOn(Date blockedOn) {
        this.blockedOn = blockedOn;
    }

}
