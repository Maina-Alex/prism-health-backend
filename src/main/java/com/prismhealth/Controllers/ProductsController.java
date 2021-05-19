package com.prismhealth.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.Category;
import com.prismhealth.Models.Photos;
import com.prismhealth.Models.Product;
import com.prismhealth.Models.SubCategory;
import com.prismhealth.repository.CategoryRepository;
import com.prismhealth.repository.SubCategoriesRepository;
import com.prismhealth.services.ProductsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@Api(tags = "Products Api")
@RestController
@RequestMapping("catalog")
@CrossOrigin
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
    @ApiOperation(value = "Get all categories")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "category not found") })
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories(){
        return ResponseEntity.ok(categoryRepository.findAll());
    }
    @ApiOperation(value = "Get category by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "category not found") })
    @GetMapping("/{categoryName}")
    public ResponseEntity<List<Category>> getCategoryByName(@PathVariable String categoryName){
        return ResponseEntity.ok(productsService.categoryByName(categoryName));
    }
    @ApiOperation(value = "Get sub-categories by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "sub-category not found") })
    @GetMapping("/subCategories/{categoryName}")
    public ResponseEntity<List<SubCategory>> getSubCategories(@PathVariable String categoryName){
        return ResponseEntity.ok(productsService.getSubcategoriesByName(categoryName));
    }
    @ApiOperation(value = "Get sub-categories ")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "sub-category not found") })
    @GetMapping("/subCategories")
    public ResponseEntity<List<SubCategory>> getAllSubCategories(){
        return ResponseEntity.ok(productsService.getAllSubcategories());
    }
    @ApiOperation(value = "Get products under a sub-category")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @GetMapping("/{subCategoryName}/products")
    public ResponseEntity<List<Product>> getSubCategoryProducts(@PathVariable String subCategoryName){
        return ResponseEntity.ok(productsService.getAllProducts(subCategoryName));
    }
    @ApiOperation(value = "Get products by name")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "Product not found") })
    @GetMapping("/products/{productName}")
    public ResponseEntity<List<Product>> getProduct(@PathVariable String productName){
        return ResponseEntity.ok(productsService.productByName(productName));
    }

    /*POST_MAPPINGS*/
    @ApiOperation(value = "Post a category")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestParam String category, @RequestParam MultipartFile multipartFile){
        return ResponseEntity.ok(productsService.saveCategory(category,multipartFile));
    }
    @ApiOperation(value = "Post a sub-category")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @PostMapping("/subCategories")
    public ResponseEntity<SubCategory> createSubCategory(@RequestParam String subCategory,@RequestParam MultipartFile multipartFile){
        return ResponseEntity.ok(productsService.saveSubCategory(subCategory,multipartFile));
    }
    @ApiOperation(value = "Post a product")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestParam String productDetails, @RequestParam("image") MultipartFile multipartFile, Principal principal){
        try {
            Product product = objectMapper.readValue(productDetails,Product.class);
            LoggerFactory.getLogger(this.getClass()).info("Products-> "+product.toString());
            return new  ResponseEntity<>(productsService.saveProduct(product,multipartFile,principal), HttpStatus.CREATED);
        }catch (Throwable e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/photos/{id}")
    public String getPhoto(@PathVariable String id, Model model) {
        Photos photo = productsService.getPhoto(id);
        model.addAttribute("image",Base64.getEncoder().encodeToString(photo.getPhoto().getData()));
        return  "image";

    }
    @GetMapping("/{productId}")
    @ResponseBody
    public ResponseEntity<?> getFile(@RequestBody String productId) {
        return productsService.getImage(productId);
    }

    @GetMapping(value = "/image",produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getImage(@RequestParam("image") String imagePath){

        var imgFile = this.getClass().getResource(imagePath);
        byte[] bytes = new byte[0];
        //bytes = StreamUtils.copyToByteArray(imgFile);
        if (imgFile==null){
            LoggerFactory.getLogger(this.getClass()).error("imgFile empty");
        }
        assert imgFile != null;
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imagePath);
        //.body(bytes);
       // return null;
    }
    /*PUT MAPPINGS*/
    @ApiOperation(value = "Update a product")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "Product not found") })
    @PutMapping("/products/{product}")
    public ResponseEntity<?> updateProduct(@PathVariable("product") Product product){
        return ResponseEntity.ok(productsService.updateProduct(product));
    }

    /*DELETE MAPPINGS*/
    @ApiOperation(value = "Delete a product")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "Product not found") })
    @DeleteMapping("/products/{product}")
    public ResponseEntity<?> deleteProduct(@PathVariable("product") Product product){
        return ResponseEntity.ok(productsService.deleteProduct(product));
    }

}
