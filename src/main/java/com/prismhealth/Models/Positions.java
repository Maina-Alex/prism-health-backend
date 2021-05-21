package com.prismhealth.Models;

import lombok.Data;

@Data
public class Positions {
    private double latitude;
    private double longitude;
    private String locationName;
    private String radius;
}
