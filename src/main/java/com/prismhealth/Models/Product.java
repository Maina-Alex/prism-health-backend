package com.prismhealth.Models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Product {
	@Id
	private String id;

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
	private List<String> photos;

	@JsonProperty("position")
	private double[] position;

	@JsonProperty("provider")
	private Users users;
	/*
	 * @Transient public String getPhotosImagePath() { if (photos == null || user ==
	 * null) return null;
	 * 
	 * return "/user-photos/" + user + "/" + photos; }
	 */

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(String productQuantity) {
		this.productQuantity = productQuantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getProductVariant() {
		return productVariant;
	}

	public void setProductVariant(String productVariant) {
		this.productVariant = productVariant;
	}

	public List<String> getPhotos() {
		return photos;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}
}