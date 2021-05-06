package com.prismhealth.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class UserRoles {
    @Id
    private String id;
    private String userId;
    private String role;
    private String assignedBy;
    private Date assignedOn;
    private boolean approved;
    private Date approvedOn;
    private String approvedBy;

    private boolean approve_delete;
    private String approve_deleteBy;
    private String deletedBy;
    private boolean deleted;

    private String created_by;
    private Date timestamp;
    private Date deletedOn;

}
