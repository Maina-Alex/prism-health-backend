package com.prismhealth.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.Category;
import com.prismhealth.Models.Product;
import com.prismhealth.Models.SubCategory;
import com.prismhealth.repository.CategoryRepository;
import com.prismhealth.repository.ProductsRepository;
import com.prismhealth.repository.SubCategoriesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductsService {
    private ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;
    private final SubCategoriesRepository subCategoriesRepository;
    private final ProductsRepository productsRepository;
    public ProductsService(ObjectMapper objectMapper1, CategoryRepository categoryRepository1, SubCategoriesRepository subCategoriesRepository1, ProductsRepository productsRepository){
        this.objectMapper = objectMapper1;
        this.categoryRepository = categoryRepository1;
        this.subCategoriesRepository = subCategoriesRepository1;
        this.productsRepository = productsRepository;
    }
    public List<SubCategory> getAllSubcategories(String subCategoryName){
        //TODO marshal up a response for when sub category does not exists
        return subCategoriesRepository.findAll()
                        .stream().filter(r-> r.getCategory().equals(subCategoryName))
                        .collect(Collectors.toList());
    }
    public List<Product> getAllProducts(String subCategoryName){
        //TODO marshal up a response for when products do not exists
        return productsRepository.findAll()
                .stream().filter(r-> r.getSubCategory().equals(subCategoryName))
                .collect(Collectors.toList());
    }

    /* getting product subCategory and category by name*/

    public List<Product> productByName(String productName){
        //TODO marshal up a response for when product does not exists
        return productsRepository.findAll()
                .stream().filter(r-> r.getProductName().equals(productName))
                .collect(Collectors.toList());
    }
    public List<SubCategory> subCategoryByName(String subCategoryName){
        //TODO marshal up a response for when product does not exists
        return subCategoriesRepository.findAll()
                .stream().filter(r-> r.getSubCategoryName().equals(subCategoryName))
                .collect(Collectors.toList());
    }
    public List<Category> categoryByName(String categoryName) {
        //TODO marshal up a response for when category does not exists
        return categoryRepository.findAll()
                .stream().filter(r -> r.getCategoryName().equals(categoryName))
                .collect(Collectors.toList());
    }

    /* saving category,subCategory and product*/
    public Category saveCategory(Category category) {
        if (categoryByName(category.getCategoryName()).isEmpty()){
            return categoryRepository.save(category);
        }
        //TODO marshal up a response for when category exists
        return null;
    }
    public SubCategory saveSubCategory(SubCategory subCategory) {
        if (!categoryByName(subCategory.getCategory()).isEmpty()){
            return subCategoriesRepository.save(subCategory);
        }
        //TODO marshal up a response for when category does not exists
        return null;
    }
    public Product saveProduct(Product product) {
        if (!subCategoryByName(product.getSubCategory()).isEmpty()){
            return productsRepository.save(product);
        }
        //TODO marshal up a response for when subCategory does not exists
        return null;
    }

    }
