package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Category {

	@JsonProperty("categoryName")
	private String categoryName;
	@Id

	private String id;

	@JsonProperty("description")
	String description;

	@JsonProperty("photos")
	private String photos;

	/*
	 * @Transient public String getPhotosImagePath() { if (photos == null ||
	 * categoryName == null) return null;
	 * 
	 * return "/user-photos/" + categoryName + "/" + photos; }
	 */

	// @JsonProperty("categorySubCategories")
	// private List<SubCategory> subCategories;
}