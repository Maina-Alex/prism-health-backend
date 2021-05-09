package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StkPushCallBackResponse{

    @JsonProperty("Body")
    private Body body;

    public Body getBody(){
        return body;
    }
}