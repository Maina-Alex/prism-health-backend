package com.prismhealth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StkCallback{

    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;

    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;

    @JsonProperty("ResultDesc")
    private String resultDesc;

    @JsonProperty("ResultCode")
    private int resultCode;

    @JsonProperty("CallbackMetadata")
    private CallbackMetadata callbackMetadata;

    public String getMerchantRequestID(){
        return merchantRequestID;
    }

    public String getCheckoutRequestID(){
        return checkoutRequestID;
    }

    public String getResultDesc(){
        return resultDesc;
    }

    public int getResultCode(){
        return resultCode;
    }

    public CallbackMetadata getCallbackMetadata(){
        return callbackMetadata;
    }
}