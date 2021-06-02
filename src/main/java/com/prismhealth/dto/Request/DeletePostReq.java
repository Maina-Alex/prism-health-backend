package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class DeletePostReq {
    @JsonProperty("postId")
    private int postId;
    @JsonProperty("categoryId")
    private String categoryId;
}
