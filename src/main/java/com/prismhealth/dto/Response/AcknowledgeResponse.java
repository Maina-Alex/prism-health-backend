package com.prismhealth.dto.Response;

import lombok.Data;

@Data
public class AcknowledgeResponse {
    private String message;
    private String ResultCode;
    private String phone;
    private String receiptNumber;
}