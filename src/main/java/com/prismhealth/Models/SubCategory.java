package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SubCategory{

	@JsonProperty("subCategoryName")
	private String subCategoryName;

	//@JsonProperty("subCategoryProducts")
	//List<Product> subCategoryProducts;

	@JsonProperty("Category")
	String category;
}