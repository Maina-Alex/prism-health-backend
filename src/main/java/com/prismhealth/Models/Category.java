package com.prismhealth.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.util.List;
@Data
public class Category {

	@JsonProperty("categoryName")
	private String categoryName;

	@JsonProperty("description")
	String description;

	@JsonProperty("photos")
	private String photos;

	@Transient
	public String getPhotosImagePath() {
		if (photos == null || categoryName == null) return null;

		return "/user-photos/" + categoryName + "/" + photos;
	}

	//@JsonProperty("categorySubCategories")
	//private List<SubCategory> subCategories;
}