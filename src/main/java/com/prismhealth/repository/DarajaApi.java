package com.prismhealth.repository;

import com.prismhealth.Models.User;
import com.prismhealth.dto.Request.OrderRequest;
import com.prismhealth.dto.Response.AccessTokenResponse;
import com.prismhealth.dto.Response.AcknowledgeResponse;
import com.prismhealth.dto.Response.StkPushCallBackResponse;
import com.prismhealth.dto.Response.StkPushSyncResponse;

public interface DarajaApi {
    AccessTokenResponse authenticate();
    StkPushSyncResponse stkPushTransaction(OrderRequest orderRequest);
    AcknowledgeResponse confirmationResults(StkPushCallBackResponse stkPushResponse);
}
