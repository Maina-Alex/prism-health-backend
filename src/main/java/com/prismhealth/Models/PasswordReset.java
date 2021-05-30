package com.prismhealth.Models;

import lombok.Data;

/**
 * PasswordReset
 */
@Data
public class PasswordReset {
    private String password;
    private String authCode;

}