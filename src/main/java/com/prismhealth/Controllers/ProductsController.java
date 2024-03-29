package com.prismhealth.Controllers;

import com.prismhealth.Models.Category;
import com.prismhealth.Models.Product;
import com.prismhealth.Models.SubCategory;
import com.prismhealth.dto.Request.*;
import com.prismhealth.repository.CategoryRepository;
import com.prismhealth.services.ProductsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import lombok.AllArgsConstructor;

@Api(tags = "Products Api")
@RestController
@RequestMapping("catalog")
@CrossOrigin
@AllArgsConstructor
public class ProductsController {
    private final CategoryRepository categoryRepository;
    private final ProductsService productsService;

    @ApiOperation(value = "Get all categories")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "category not found") })
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @PostMapping("/category/enable/{name}")
    public ResponseEntity<?> enableCategory(@PathVariable String name) {
        return productsService.enableCategory(name);
    }

    @PostMapping("/category/disable/{name}")
    public ResponseEntity<?> deleteCategory(@PathVariable String name) {
        return productsService.disableCategory(name);
    }

    @ApiOperation(value = "Get category by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "category not found") })
    @GetMapping("/{categoryName}")
    public Category getCategoryByName(@PathVariable String categoryName) {
        return productsService.categoryByName(categoryName);
    }

    @ApiOperation(value = "Get category by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "category not found") })
    @PostMapping("/updateCategory")
    public Category updateCategoryByName(@RequestBody UpdateCategoryRequest req) {
        return productsService.updateCategory(req);
    }

    @ApiOperation(value = "Get sub-categories by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "sub-category not found") })
    @GetMapping("/subCategories/{categoryName}")
    public List<SubCategory> getSubCategories(@PathVariable String categoryName) {
        return productsService.getSubcategoriesByName(categoryName);
    }

    @ApiOperation(value = "Get sub-categories ")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "sub-category not found") })
    @GetMapping("/subCategories")
    public List<SubCategory> getAllSubCategories() {
        return productsService.getAllSubcategories();
    }

    @PostMapping("/subCategory/enable")
    public ResponseEntity<?> enableSubCategory(@RequestBody UpdateSubCategoryReq req) {
        return productsService.enableSubCategory(req);
    }

    @PostMapping("/subCategory/disable")
    public ResponseEntity<?> disableCategory(@RequestBody UpdateSubCategoryReq req) {
        return productsService.disableSubCategory(req);
    }

    @PostMapping("/subCategory/update")
    public ResponseEntity<?> updateSubCategory(@RequestBody UpdateSubCategoryReq req) {
        return productsService.updateSubCategory(req);
    }

    @ApiOperation(value = "Get products under a sub-category")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @GetMapping("/{subCategoryName}/products")
    public List<Product> getSubCategoryProducts(@PathVariable String subCategoryName) {
        return productsService.getAllProducts(subCategoryName);
    }

    @ApiOperation(value = "Get products by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "Product not found") })
    @GetMapping("/products/{productName}")
    public List<Product> getProduct(@PathVariable String productName) {
        return productsService.productByName(productName);
    }

    @GetMapping("/getAllAvailableProducts")
    public List<Product> getAllProducts() {
        return productsService.getAllAvailableProducts();
    }

    @GetMapping("/productByProviderId/{providerId}")
    public List<Product> getProductByProviderId(@PathVariable String providerId) {
        return productsService.getProductsByProviderId(providerId);
    }

    @GetMapping("/productByProvider")
    public List<Product>getProductByProvider(Principal principal) {
        return productsService.getProductsByProviderId(principal.getName());
    }

    /* POST_MAPPINGS */
    @ApiOperation(value = "Post a category")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        return productsService.saveCategory(request);
    }

    @ApiOperation(value = "Post a sub-category")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @PostMapping("/subCategories")
    public ResponseEntity<?> createSubCategory(@RequestBody SubCategoryRequest request) {
        return productsService.saveSubCategory(request);
    }

    @PostMapping("/subcategory")
    public ResponseEntity<?> getSubCategory(@RequestBody UpdateSubCategoryReq request) {
        return productsService.getSubCategoryByName(request);
    }

    @ApiOperation(value = "Post a product")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest request, Principal principal) {
        return productsService.saveProduct(request, principal);
    }

    @GetMapping("/product/{id}")
    public Product findProductById(@PathVariable String id) {
        return productsService.getProductById(id);
    }

    @PostMapping("/product/{id}")
    public void deleteProductById(@PathVariable String id) {
        productsService.deleteProduct(id);
    }

    /* PUT MAPPINGS */
    @ApiOperation(value = "Update a product")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "Product not found") })
    @PutMapping

    public ResponseEntity<?> updateProducts(@RequestBody Product product) {
        return productsService.updateProduct(product);
    }

    /* DELETE MAPPINGS */
    @ApiOperation(value = "Delete a product")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "Product not found") })
    @DeleteMapping("/products/{productid}")
    public ResponseEntity<?> deleteProducts(@PathVariable("productid") String productid) {
        return productsService.deleteProduct(productid);
    }

}
