package com.prismhealth.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.Category;
import com.prismhealth.Models.Product;
import com.prismhealth.Models.SubCategory;
import com.prismhealth.Models.Variant;
import com.prismhealth.repository.CategoryRepository;
import com.prismhealth.repository.ProductsRepository;
import com.prismhealth.repository.SubCategoriesRepository;
import com.prismhealth.repository.VariantRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductsService {
    private final VariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoriesRepository subCategoriesRepository;
    private final ProductsRepository productsRepository;
    public ProductsService(VariantRepository variantRepository, CategoryRepository categoryRepository1, SubCategoriesRepository subCategoriesRepository1, ProductsRepository productsRepository){
        this.variantRepository = variantRepository;
        this.categoryRepository = categoryRepository1;
        this.subCategoriesRepository = subCategoriesRepository1;
        this.productsRepository = productsRepository;
    }
    public List<SubCategory> getAllSubcategories(String categoryName){
        //TODO marshal up a response for when sub category does not exists
        return subCategoriesRepository.findAll()
                        .stream().filter(r-> r.getCategory().equals(categoryName))
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
                .stream().filter(r-> r.getProductName().contains(productName))
                .collect(Collectors.toList());
    }
    public List<SubCategory> subCategoryByName(String subCategoryName){
        //TODO marshal up a response for when product does not exists
        return subCategoriesRepository.findAll()
                .stream().filter(r-> r.getSubCategoryName().contains(subCategoryName))
                .collect(Collectors.toList());
    }
    public List<Category> categoryByName(String categoryName) {
        //TODO marshal up a response for when category does not exists
        return categoryRepository.findAll()
                .stream().filter(r -> r.getCategoryName().contains(categoryName))
                .collect(Collectors.toList());
    }
    public List<Variant> variantByName(String variantName){
    return variantRepository.findAll()
            .stream().filter(variant -> variant.getVariantName().contains(variantName)).collect(Collectors.toList());
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
        Variant variant = new Variant();
        variant.setVariantName(product.getProductVariant());
        variant.setSubCategory(product.getSubCategory());
        if (!subCategoryByName(product.getSubCategory()).isEmpty()){
            if (variantByName(product.getProductVariant()).isEmpty())
                variantRepository.save(variant);
            return productsRepository.save(product);
        }
        //TODO marshal up a response for when subCategory does not exists
        return null;
    }
    public Variant createVariant(Variant variant) {
        if (!subCategoryByName(variant.getSubCategory()).isEmpty()){
            return variantRepository.save(variant);
        }
        //TODO marshal up a response for when subCategory does not exists
        return null;
    }


    public ResponseEntity<?> deleteProduct(Product product) {
        List<?> products = productsRepository.findAll().stream().filter(
                product1 -> product1.getProductName()
                        .equals(product.getProductName())&&
                        product1.getSubCategory().equals(product.getSubCategory())).collect(Collectors.toList());

        if (products.isEmpty()){
            try {
                throw new ChangeSetPersister.NotFoundException();
            } catch (ChangeSetPersister.NotFoundException e) {
                e.printStackTrace();
            }
        }
        productsRepository.delete(product);
        return ResponseEntity.ok().body(product.getProductName()+" Successfully deleted");
    }
    public ResponseEntity<?> updateProduct(Product product) {
        List<?> products = productsRepository.findAll().stream().filter(
                product1 -> product1.getProductName()
                        .equals(product.getProductName())&&
                        product1.getSubCategory().equals(product.getSubCategory())).collect(Collectors.toList());
        if (products.isEmpty()){
            try {
                throw new ChangeSetPersister.NotFoundException();
            } catch (ChangeSetPersister.NotFoundException e) {
                e.printStackTrace();
            }
        }

        productsRepository.save(product);
        return ResponseEntity.ok().body(product.getProductName()+" Successfully deleted");
    }
}
