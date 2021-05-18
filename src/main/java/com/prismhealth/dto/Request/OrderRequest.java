package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prismhealth.Models.Product;
import com.prismhealth.Models.Users;
import lombok.Data;

@Data
public class OrderRequest{

	@JsonProperty("product")
	private Product product;

	@JsonProperty("quantity")
	private String quantity;

	@JsonProperty("user")
	private Users users;
}