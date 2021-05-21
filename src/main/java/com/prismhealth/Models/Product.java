package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Product {

	@JsonProperty("productQuantity")
	private String productQuantity;

	@JsonProperty("productName")
	private String productName;

	@JsonProperty("productPrice")
	private String productPrice;

	@JsonProperty("productDescription")
	private String productDescription;

	@JsonProperty("productProvider")
	private String user;

	@JsonProperty("subCategory")
	private String subCategory;

	@JsonProperty("productVariant")
	private String productVariant;

	@JsonProperty("photos")
	private String photos;

	@JsonProperty("provider")
	private Users users;
	/*
	 * @Transient public String getPhotosImagePath() { if (photos == null || user ==
	 * null) return null;
	 * 
	 * return "/user-photos/" + user + "/" + photos; }
	 */
}