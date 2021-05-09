package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Body{

    @JsonProperty("stkCallback")
    private StkCallback stkCallback;

    public StkCallback getStkCallback(){
        return stkCallback;
    }
}
