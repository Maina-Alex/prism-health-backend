package com.prismhealth.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.List;

@Data
@Document
public class Category {

	@Id
	private String id;
	private String categoryType;
	private String categoryName;
	private String description;
	private String photo;
	private List<SubCategory>subCategories;
}