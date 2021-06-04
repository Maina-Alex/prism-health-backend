package com.prismhealth.Controllers;

import com.prismhealth.Models.Category;
import com.prismhealth.Models.Product;
import com.prismhealth.Models.SubCategory;
import com.prismhealth.dto.Request.CategoryRequest;
import com.prismhealth.dto.Request.ProductCreateRequest;
import com.prismhealth.dto.Request.SubCategoryRequest;
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
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @ApiOperation(value = "Get category by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "category not found") })
    @GetMapping("/{categoryName}")
    public ResponseEntity<List<Category>> getCategoryByName(@PathVariable String categoryName) {
        return ResponseEntity.ok(productsService.categoryByName(categoryName));
    }

    @ApiOperation(value = "Get sub-categories by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "sub-category not found") })
    @GetMapping("/subCategories/{categoryName}")
    public ResponseEntity<List<SubCategory>> getSubCategories(@PathVariable String categoryName) {
        return ResponseEntity.ok(productsService.getSubcategoriesByName(categoryName));
    }

    @ApiOperation(value = "Get sub-categories ")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "sub-category not found") })
    @GetMapping("/subCategories")
    public ResponseEntity<List<SubCategory>> getAllSubCategories() {
        return ResponseEntity.ok(productsService.getAllSubcategories());
    }

    @ApiOperation(value = "Get products under a sub-category")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @GetMapping("/{subCategoryName}/products")
    public ResponseEntity<List<Product>> getSubCategoryProducts(@PathVariable String subCategoryName) {
        return ResponseEntity.ok(productsService.getAllProducts(subCategoryName));
    }

    @ApiOperation(value = "Get products by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "Product not found") })
    @GetMapping("/products/{productName}")
    public ResponseEntity<List<Product>> getProduct(@PathVariable String productName) {
        return ResponseEntity.ok(productsService.productByName(productName));
    }

    @GetMapping("/getAllAvailableProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productsService.getAllAvailableProducts());
    }

    @GetMapping("/productByProviderId/{providerId}")
    public ResponseEntity<List<Product>> getProductByProviderId(@PathVariable String providerId) {
        return ResponseEntity.ok(productsService.getProductsByProviderId(providerId));
    }

    @GetMapping("/productByProvider")
    public ResponseEntity<List<Product>> getProductByProvider(Principal principal) {
        return ResponseEntity.ok(productsService.getProductsByProviderId(principal.getName()));
    }

    /* POST_MAPPINGS */
    @ApiOperation(value = "Post a category")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(productsService.saveCategory(request));
    }

    @ApiOperation(value = "Post a sub-category")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @PostMapping("/subCategories")
    public ResponseEntity<?> createSubCategory(@RequestBody SubCategoryRequest request) {
        return productsService.saveSubCategory(request);
    }

    @ApiOperation(value = "Post a product")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateRequest request, Principal principal) {
        return productsService.saveProduct(request, principal);
    }

    /* PUT MAPPINGS */
    @ApiOperation(value = "Update a product")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "Product not found") })
    @PutMapping("/products")
    public ResponseEntity<?> updateProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productsService.updateProduct(product));
    }

    /* DELETE MAPPINGS */
    @ApiOperation(value = "Delete a product")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "Product not found") })
    @DeleteMapping("/products/{productid}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productid") String productid) {
        return ResponseEntity.ok(productsService.deleteProduct(productid));
    }

    @DeleteMapping("/category/{categoryName}")
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryName") String categoryName) {
        return ResponseEntity.ok(productsService.deleteCategory(categoryName));
    }
    // @DeleteMapping("/subCategory/{subCategoryName}")
    // public ResponseEntity<?> deleteSubCategory(@PathVariable("subCategoryName")
    // String subCategoryName) {
    // return ResponseEntity.ok(productsService.deleteSubCategory(subCategoryName));
    // }

}
