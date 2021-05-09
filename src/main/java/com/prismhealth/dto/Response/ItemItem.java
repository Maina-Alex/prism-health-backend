package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemItem{

    @JsonProperty("Value")
    private String value;

    @JsonProperty("Name")
    private String name;

    public String getValue(){
        return value;
    }

    public String getName(){
        return name;
    }
}