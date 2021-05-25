package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Phone {
    @JsonProperty("phone")
    String phone;
}
