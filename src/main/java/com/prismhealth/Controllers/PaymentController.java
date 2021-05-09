package com.prismhealth.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.dto.Request.OrderRequest;
import com.prismhealth.dto.Response.AcknowledgeSaf;
import com.prismhealth.dto.Response.StkPushCallBackResponse;
import com.prismhealth.dto.Response.StkPushSyncResponse;
import com.prismhealth.repository.DarajaApi;
import com.prismhealth.services.PaymentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("payments")
public class PaymentController {
    private final DarajaApi darajaApi;

    public PaymentController(DarajaApi darajaApi) {
        this.darajaApi = darajaApi;

    }

    @PostMapping(path = "/process",produces = "application/json")
    ResponseEntity<StkPushSyncResponse> processPayment(@RequestBody OrderRequest orderRequest){
        return ResponseEntity.ok(darajaApi.stkPushTransaction(orderRequest));
    }
    @SneakyThrows
    @PostMapping(path = "/confirm",produces = "application/json")
    ResponseEntity<AcknowledgeSaf> acknowledgeResponse(@RequestBody StkPushCallBackResponse stkPushResponse){
        AcknowledgeSaf acknowledgeSaf= new AcknowledgeSaf();
        acknowledgeSaf.setMessage("success");
        ObjectMapper mapper = new ObjectMapper();
        log.info(mapper.writeValueAsString(stkPushResponse));
        darajaApi.confirmationResults(stkPushResponse);
        return ResponseEntity.ok(acknowledgeSaf);
    }
}