package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class phone {
    @JsonProperty("phone")
    String phone;
}
