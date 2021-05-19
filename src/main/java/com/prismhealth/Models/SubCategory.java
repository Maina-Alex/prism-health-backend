package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.util.List;

@Data
public class SubCategory{

	@JsonProperty("subCategoryName")
	private String subCategoryName;

	//@JsonProperty("subCategoryProducts")
	//List<Product> subCategoryProducts;

	@JsonProperty("Category")
	String category;

	@JsonProperty("description")
	String description;

	@JsonProperty("photos")
	private String photos;

	@Transient
	public String getPhotosImagePath() {
		if (photos == null || subCategoryName == null) return null;

		return "/user-photos/" + subCategoryName + "/" + photos;
	}
}