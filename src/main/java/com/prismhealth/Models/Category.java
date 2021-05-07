package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class Category {

	@JsonProperty("categoryName")
	private String categoryName;

	//@JsonProperty("categorySubCategories")
	//private List<SubCategory> subCategories;
}