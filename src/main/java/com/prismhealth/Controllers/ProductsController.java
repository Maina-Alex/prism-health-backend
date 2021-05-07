package com.prismhealth.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.Category;
import com.prismhealth.Models.Product;
import com.prismhealth.Models.SubCategory;
import com.prismhealth.repository.CategoryRepository;
import com.prismhealth.repository.SubCategoriesRepository;
import com.prismhealth.services.ProductsService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("catalog")
public class ProductsController {
    private ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;
    private final SubCategoriesRepository subCategoriesRepository;
    private final ProductsService productsService;
    public ProductsController(ObjectMapper objectMapper, CategoryRepository categoryRepository,
                              SubCategoriesRepository subCategoriesRepository, ProductsService productsService){
        this.objectMapper = objectMapper;
        this.categoryRepository = categoryRepository;
        this.subCategoriesRepository = subCategoriesRepository;
        this.productsService = productsService;
    }
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories(){
        return ResponseEntity.ok(categoryRepository.findAll());
    }
    @GetMapping("/{categories}")
    public ResponseEntity<List<Category>> getCategoryByName(@PathVariable String categoryName){
        return ResponseEntity.ok(productsService.categoryByName(categoryName));
    }

    @GetMapping("/categories/{subCategoryName}")
    public ResponseEntity<List<SubCategory>> getAllSubCategories(@PathVariable String subCategoryName){
        return ResponseEntity.ok(productsService.getAllSubcategories(subCategoryName));
    }
    @GetMapping("/{subCategoryName}/products")
    public ResponseEntity<List<Product>> getSubCategoryProducts(@PathVariable String subCategoryName){
        return ResponseEntity.ok(productsService.getAllProducts(subCategoryName));
    }
    @GetMapping("/products/{productName}")
    public ResponseEntity<List<Product>> getProduct(@PathVariable String productName){
        return ResponseEntity.ok(productsService.productByName(productName));
    }

    /*POST_MAPPINGS*/
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category){
        return ResponseEntity.ok(productsService.saveCategory(category));
    }
    @PostMapping("/subCategories")
    public ResponseEntity<SubCategory> createSubCategory(@RequestBody SubCategory subCategory){
        return ResponseEntity.ok(productsService.saveSubCategory(subCategory));
    }
    @PostMapping("/products")
    public ResponseEntity<Product> createCategory(@RequestBody Product product){
        return ResponseEntity.ok(productsService.saveProduct(product));
    }

}
