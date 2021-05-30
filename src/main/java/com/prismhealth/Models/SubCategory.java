package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class SubCategory {

	@JsonProperty("subCategoryName")
	private String subCategoryName;

	// @JsonProperty("subCategoryProducts")
	// List<Product> subCategoryProducts;

	@JsonProperty("Category")
	String category;

	@JsonProperty("description")
	String description;

	@JsonProperty("photos")
	private String photos;

	/*
	 * @Transient public String getPhotosImagePath() { if (photos == null ||
	 * subCategoryName == null) return null;
	 * 
	 * return "/user-photos/" + subCategoryName + "/" + photos; }
	 */

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}
}