package com.prismhealth.Models;

import lombok.Data;

@Data
public class ServiceBooking {
    private String day;
    private int hour;
    private boolean available;

}
