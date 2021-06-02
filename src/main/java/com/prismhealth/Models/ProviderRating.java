package com.prismhealth.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
public class ProviderRating {
    private double averageRate;
    private List<UserRating> ratings;
}
