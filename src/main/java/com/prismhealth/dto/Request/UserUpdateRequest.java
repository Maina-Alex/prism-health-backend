package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@JsonIgnoreProperties
@NoArgsConstructor
public class UserUpdateRequest {
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
    @JsonProperty("dateOfBirth")
    private Date dateOfBirth;
    @JsonProperty("gender")
    private String gender;

}
