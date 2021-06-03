package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class UserReview {
    private String id;
    @JsonProperty("userId")
    private String userPhone;
    @JsonProperty("review")
    private String review;
    @JsonProperty("rating")
    private double rating;

}
