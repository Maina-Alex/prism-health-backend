package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UwaziiSmsRequest{

	@JsonProperty("ApiKey")
	private String apiKey;

	@JsonProperty("Message")
	private String message;

	@JsonProperty("MobileNumbers")
	private String mobileNumbers;

	@JsonProperty("ClientId")
	private String clientId;

	@JsonProperty("SenderId")
	private String senderId;

}