package com.prismhealth.services;

import com.prismhealth.Models.*;
import com.prismhealth.repository.*;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class ProductsService {
    private final VariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoriesRepository subCategoriesRepository;
    private final ProductsRepository productsRepository;
    private final AccountRepository accountRepository;
    public ProductsService(VariantRepository variantRepository, CategoryRepository categoryRepository1, SubCategoriesRepository subCategoriesRepository1, ProductsRepository productsRepository, AccountRepository accountRepository){
        this.variantRepository = variantRepository;
        this.categoryRepository = categoryRepository1;
        this.subCategoriesRepository = subCategoriesRepository1;
        this.productsRepository = productsRepository;
        this.accountRepository = accountRepository;
    }
    public List<SubCategory> getSubcategoriesByName(String categoryName){
        //TODO marshal up a response for when sub category does not exists
        return subCategoriesRepository.findAll()
                        .stream().filter(r-> r.getCategory().equals(categoryName))
                        .collect(Collectors.toList());
    }
    public List<SubCategory> getAllSubcategories(){
        //TODO marshal up a response for when sub category does not exists
        return new ArrayList<>(subCategoriesRepository.findAll());
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
    public Product saveProduct(Product product, MultipartFile multipartFile, Principal principal) {
        Users users = accountRepository.findOneByPhone(principal.getName());
        Variant variant = new Variant();
        variant.setVariantName(product.getProductVariant());
        variant.setSubCategory(product.getSubCategory());

        if (!subCategoryByName(product.getSubCategory()).isEmpty()){
            if (variantByName(product.getProductVariant()).isEmpty())
                variantRepository.save(variant);
            LoggerFactory.getLogger(getClass()).info("->>"+product.toString());
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            product.setPhotos(fileName);
            product.setUser(users.getPhone());
            String uploadDir = "user-photos/" + product.getUser();
            saveFile(uploadDir, fileName, multipartFile);
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
    public static void saveFile(String uploadDir, String fileName,
                                MultipartFile multipartFile)  {
        try{
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

         InputStream inputStream = multipartFile.getInputStream();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch ( IOException ioe) {
            try {
                throw new IOException("Could not save image file: " + fileName, ioe);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
