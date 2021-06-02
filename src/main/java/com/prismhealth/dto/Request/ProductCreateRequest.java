package com.prismhealth.dto.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prismhealth.Models.Users;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties
@AllArgsConstructor
public class ProductCreateRequest {
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
    private String providerPhone;

}
