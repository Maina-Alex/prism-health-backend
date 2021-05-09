package com.prismhealth.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.Product;
import com.prismhealth.Models.User;
import com.prismhealth.config.MpesaConfiguration;
import com.prismhealth.dto.Request.OrderRequest;
import com.prismhealth.dto.Request.StkPushRequest;
import com.prismhealth.dto.Response.AccessTokenResponse;
import com.prismhealth.dto.Response.AcknowledgeResponse;
import com.prismhealth.dto.Response.StkPushCallBackResponse;
import com.prismhealth.dto.Response.StkPushSyncResponse;
import com.prismhealth.repository.DarajaApi;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static com.prismhealth.util.ConstantsClass.*;
import static com.prismhealth.util.HelperUtility.*;

@Service
public class PaymentService implements DarajaApi {
    static Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final MpesaConfiguration mConfig;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private OrderRequest orderRequest;
    private final ProductsService productsService;

    public PaymentService(MpesaConfiguration mConfig, OkHttpClient okHttpClient, ObjectMapper objectMapper, ProductsService productsService) {
        this.mConfig = mConfig;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.productsService = productsService;
    }

    @Override
    public AccessTokenResponse authenticate() {
        String appKeySecret = toBase64String(String.format("%s:%s",mConfig.getConsumerKey(),mConfig.getConsumerSecret()));
        log.info("Transaction begin..");
        Request request = new Request.Builder()
                .url(mConfig.getOAuthUrlEndpoint())
                .get()
                .addHeader(AUTHORIZATION_HEADER_STRING,String.format("%s %s",BASIC_AUTH_STRING,appKeySecret))
                .addHeader(CACHE_CONTROL_HEADER,CACHE_CONTROL_HEADER_VALUE)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            if (!result.isEmpty()){
                return objectMapper.readValue(result, AccessTokenResponse.class);}
            else log.error(" Results is empty..");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public StkPushSyncResponse stkPushTransaction(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
        User user = orderRequest.getUser();
        Product product = orderRequest.getProduct();
        String businessShortCode=mConfig.getBusinessShortCode();
        String timestamp =getTimeStamp();
        String amount = calculateAmount(product.getProductPrice(),orderRequest.getQuantity());
        String phoneNumber= user.getPhone().contains("+")?user.getPhone().substring(1):user.getPhone();

        StkPushRequest stkRequest = new StkPushRequest();
        stkRequest.setBusinessShortCode(businessShortCode);
        stkRequest.setPassword(getStkPushPassword(businessShortCode,mConfig.getPassKey(),timestamp));
        stkRequest.setTimestamp(timestamp);
        stkRequest.setTransactionType(TRANSACTION_TYPE);
        stkRequest.setAmount(amount);
        stkRequest.setPhoneNumber(phoneNumber);
        stkRequest.setPartyA(phoneNumber);
        stkRequest.setPartyB(businessShortCode);
        stkRequest.setCallBackURL(mConfig.getCallBackUrl());
        stkRequest.setAccountReference(getTransactionUniqueNumber());
        stkRequest.setTransactionDesc("Payment for product");

        AccessTokenResponse accessTokenResponse = authenticate();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, Objects.requireNonNull(toJson(stkRequest)));

        Request request = new Request.Builder()
                .url(mConfig.getStkPushUrl())
                .post(body)
                .addHeader("authorization","Bearer "+accessTokenResponse.getAccessToken())
                .addHeader("content-type", "application/json")
                .build();
        //https://sandbox.safaricom.co.ke/mpesa/stkpushquery/v1/query

        try {
            Response response = okHttpClient.newCall(request).execute();
            log.info("Request successful..");

            return objectMapper.readValue(Objects.requireNonNull(response.body()).string(),StkPushSyncResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not perform the stk push -> %s",e.getLocalizedMessage()));
            return null;
        }

    }

    public AcknowledgeResponse confirmationResults(StkPushCallBackResponse response){
        AcknowledgeResponse acknowledgeResponse = new AcknowledgeResponse();
        if (response.getBody().getStkCallback().getResultCode()== 0){
        acknowledgeResponse.setMessage(response.getBody().getStkCallback().getResultDesc());
        acknowledgeResponse.setResultCode(String.valueOf(response.getBody().getStkCallback().getResultCode()));
        acknowledgeResponse.setPhone(response.getBody().getStkCallback().getCallbackMetadata().getItem().get(4).getValue());
        acknowledgeResponse.setReceiptNumber(response.getBody().getStkCallback().getCallbackMetadata().getItem().get(2).getValue());
        log.info(orderRequest.toString());
        //TODO persist the response into the database.

        return acknowledgeResponse;}
        else {
            acknowledgeResponse.setMessage(response.getBody().getStkCallback().getResultDesc());
            acknowledgeResponse.setResultCode(String.valueOf(response.getBody().getStkCallback().getResultCode()));
            return acknowledgeResponse;
        }
    }
}
