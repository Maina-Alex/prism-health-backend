package com.prismhealth.dto.Request;

import lombok.Data;

@Data
public class ServiceBooking {
    private String day;
    private int hour;
    private boolean available;
    private String status;

}
