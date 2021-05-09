package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AcknowledgeSaf {
    @JsonProperty("Message")
    String message;
}
