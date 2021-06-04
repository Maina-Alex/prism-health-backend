package com.prismhealth.services;

import com.prismhealth.Models.*;

import com.prismhealth.dto.Request.*;
import com.prismhealth.repository.*;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import java.security.Principal;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductsService {
    private final CategoryRepository categoryRepository;
    private final ProductsRepository productsRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<SubCategory> getSubcategoriesByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName).get().getSubCategories();
    }

    public List<SubCategory> getAllSubcategories() {
        List<SubCategory> subCategories = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAll();
        for (Category c : categoryList) {
            if (c.getSubCategories() != null) {
                subCategories.addAll(c.getSubCategories());
            }
        }
        return subCategories;
    }

    public List<Product> getAllProducts(String subCategoryName) {
        List<Product> products = productsRepository.findAll().stream()
                .filter(r -> r.getSubCategory().equalsIgnoreCase(subCategoryName)).collect(Collectors.toList());
        for (Product product : products) {
            product.setUsers(userRepository.findByPhone(product.getUser()));
        }
        return products;
    }

    /* getting product subCategory and category by name */

    public List<Product> productByName(String productName) {
        // TODO marshal up a response for when product does not exists
        List<Product> products = productsRepository.findAll().stream()
                .filter(r -> r.getProductName().contains(productName)).collect(Collectors.toList());
        for (Product product : products) {
            product.setUsers(userRepository.findByPhone(product.getUser()));
        }
        return products;
    }

    public ResponseEntity<?> enableCategory(String name) {
        Optional<Category> category = categoryRepository.findByCategoryName(name);
        if (category.isPresent()) {
            Category cat = category.get();
            cat.setDisabled(false);
            categoryRepository.save(cat);
            return ResponseEntity.ok(cat);
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Not modified");
    }

    public ResponseEntity<?> disableCategory(String name) {
        Optional<Category> category = categoryRepository.findByCategoryName(name);
        if (category.isPresent()) {
            Category cat = category.get();
            cat.setDisabled(true);
            categoryRepository.save(cat);
            return ResponseEntity.ok(cat);
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Not modified");
    }

    public Category categoryByName(String categoryName) {
        // TODO marshal up a response for when category does not exists
        return categoryRepository.findAll().stream().filter(c -> c.getCategoryName().equalsIgnoreCase(categoryName))
                .findAny().orElse(null);
    }

    public Category updateCategory(UpdateCategoryRequest req) {
        Category cat = categoryRepository.findAll().stream()
                .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getOldName())).findAny().orElse(null);
        ;
        if (cat != null) {
            if (!req.getCategoryName().equals(""))
                cat.setCategoryName(req.getCategoryName());
            if (!req.getCategoryType().equals(""))
                cat.setCategoryType(req.getCategoryType());
            if (!req.getDescription().equals(""))
                cat.setDescription(req.getDescription());
            if (!req.getPhoto().equals(""))
                cat.setPhoto(req.getPhoto());
            return categoryRepository.save(cat);
        }
        return null;
    }

    /* saving category,subCategory and product */
    public Category saveCategory(CategoryRequest req) {
        Optional<Category> cat = categoryRepository.findByCategoryName(req.getCategoryName());

        if (!cat.isPresent()) {
            Category category = new Category();
            category.setCategoryName(req.getCategoryName());
            category.setCategoryType(req.getCategoryType());
            category.setDescription(req.getDescription());
            category.setPhoto(req.getPhoto());
            return categoryRepository.save(category);

        }

        // TODO marshal up a response for when category exists
        return null;
    }

    public ResponseEntity<?> saveSubCategory(SubCategoryRequest req) {
        SubCategory subCategory = new SubCategory();

        Optional<Category> category = categoryRepository.findByCategoryName(req.getCategoryName());
        if (category.isPresent()) {

            Optional<List<SubCategory>> subOp = Optional.ofNullable(category.get().getSubCategories());
            List<SubCategory> subCategories = subOp.orElse(new ArrayList<SubCategory>());
            subCategory.setCategoryName(category.get().getCategoryName());
            subCategory.setSubCategoryName(req.getSubCategoryName());

            subCategory.setDescription(req.getDescription());
            subCategory.setPhotos(req.getPhoto());
            subCategories.add(subCategory);
            category.get().setSubCategories(subCategories);
            categoryRepository.save(category.get());
            return ResponseEntity.ok().body(subCategory);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category not found");
    }

    public ResponseEntity<?> updateSubCategory(UpdateSubCategoryReq req) {
        Category category = categoryRepository.findAll().stream()
                .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getCategoryName())).findAny().orElse(null);
        if (category != null) {
            List<SubCategory> subCategoryList = category.getSubCategories();
            SubCategory sub = subCategoryList.stream()
                    .filter(s -> s.getSubCategoryName().equalsIgnoreCase(req.getOldName())).findAny().orElse(null);
            if (sub != null) {
                if (!req.getSubCategoryName().equals(""))
                    sub.setSubCategoryName(req.getSubCategoryName());
                if (!req.getDescription().equals(""))
                    sub.setDescription(req.getDescription());
                if (!req.getPhoto().equals(""))
                    sub.setPhotos(req.getPhoto());
                subCategoryList.remove(sub);
                subCategoryList.add(sub);
                category.setSubCategories(subCategoryList);
                categoryRepository.save(category);
                return new ResponseEntity<>(HttpStatus.OK);
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Sub Category not modified");
    }

    public ResponseEntity<?> enableSubCategory(UpdateSubCategoryReq req) {
        Category category = categoryRepository.findAll().stream()
                .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getCategoryName())).findAny().orElse(null);
        if (category != null) {
            List<SubCategory> subCategoryList = category.getSubCategories();
            SubCategory sub = subCategoryList.stream()
                    .filter(s -> s.getSubCategoryName().equalsIgnoreCase(req.getOldName())).findAny().orElse(null);
            if (sub != null) {
                sub.setDisabled(false);
                subCategoryList.remove(sub);
                subCategoryList.add(sub);
                category.setSubCategories(subCategoryList);
                categoryRepository.save(category);
                return new ResponseEntity<>(HttpStatus.OK);
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Sub Category not modified");
    }

    public ResponseEntity<?> disableSubCategory(UpdateSubCategoryReq req) {
        Category category = categoryRepository.findAll().stream()
                .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getCategoryName())).findAny().orElse(null);
        if (category != null) {
            List<SubCategory> subCategoryList = category.getSubCategories();
            SubCategory sub = subCategoryList.stream()
                    .filter(s -> s.getSubCategoryName().equalsIgnoreCase(req.getOldName())).findAny().orElse(null);
            if (sub != null) {
                sub.setDisabled(true);
                subCategoryList.remove(sub);
                subCategoryList.add(sub);
                category.setSubCategories(subCategoryList);
                categoryRepository.save(category);
                return new ResponseEntity<>(HttpStatus.OK);
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Sub Category not modified");
    }

    public List<Product> getAllAvailableProducts() {
        return productsRepository.findAll();
    }

    public List<Product> getProductsByProviderId(String providerId) {
        return productsRepository.findAllByUser(providerId);
    }

    public List<Product> getProductsByProvider(Principal principal) {

        return productsRepository.findAllByUser(principal.getName());
    }

    public ResponseEntity<?> deleteCategory(String categoryName) {
        categoryRepository.delete(categoryRepository.findByCategoryName(categoryName).get());
        return ResponseEntity.ok().body("Successfully deleted..");
    }

    public ResponseEntity<?> saveProduct(ProductCreateRequest req, Principal principal) {
        String phoneNumber = "";
        if (req.getProviderPhone() != null && !req.getProviderPhone().equals("")) {
            phoneNumber = req.getProviderPhone();
        } else {
            phoneNumber = principal.getName();
        }

        Users users = userRepository.findByPhone(phoneNumber);
        if (users != null) {
            if (!users.getAccountType().equals("USER")) {
                Product product = new Product();
                product.setProductName(req.getProductName());
                product.setSubCategory(req.getSubCategory());
                product.setProductDescription(req.getProductDescription());
                product.setProductQuantity(req.getProductQuantity());
                product.setProductPrice(req.getProductPrice());
                product.setPhotos(req.getPhotos());
                product.setPosition(req.getPosition());
                product.setUser(users.getPhone());
                product.setUsers(users);
                productsRepository.save(product);
                return ResponseEntity.status(HttpStatus.CREATED).body(product);
            }

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User forbidden to do this action");
    }

    public Photos getPhoto(String id) {
        return photoRepository.findById(id).get();
    }

    public ResponseEntity<?> deleteProduct(String id) {

        Optional<Product> prod = productsRepository.findById(id);

        if (prod.isPresent()) {
            productsRepository.delete(prod.get());
            return ResponseEntity.ok().body(prod.get());
        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("product not found");

    }

    public ResponseEntity<?> updateProduct(Product product) {

        productsRepository.save(product);
        return ResponseEntity.ok().body(product.getProductName() + " Successfully deleted");
    }

    public ResponseEntity<?> getSubCategoryByName(UpdateSubCategoryReq request) {
        return null;
    }

    public Product getProductById(String id) {
        Optional<Product> prod = productsRepository.findById(id);
        if (prod.isPresent()) {
            return prod.get();
        }
        return null;

    }

}
