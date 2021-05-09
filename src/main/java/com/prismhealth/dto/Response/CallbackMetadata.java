package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CallbackMetadata{

    @JsonProperty("Item")
    private List<ItemItem> item;

    public List<ItemItem> getItem(){
        return item;
    }
}
