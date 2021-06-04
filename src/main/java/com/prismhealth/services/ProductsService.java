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

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<SubCategory> getSubcategoriesByName(String categoryName) {
      Category cat= categoryRepository.findAll().stream()
                .filter(c->c.getCategoryName()!=null)
                .filter(c -> c.getCategoryName().equalsIgnoreCase(categoryName))
                .findAny().orElse(null);
        if(cat!=null) return  cat.getSubCategories();
        return new ArrayList<>();
    }

    public List<SubCategory> getAllSubcategories() {
        List<SubCategory> subCategories = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAll();
        for (Category c : categoryList) {
            subCategories.addAll(c.getSubCategories());
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

    public ResponseEntity<?>enableCategory(String name){
        Category category=categoryRepository.findAll().stream()
                .filter(c->c.getCategoryName()!=null)
                .filter(c -> c.getCategoryName().equalsIgnoreCase(name))
                .findAny().orElse(null);
        if(category!=null){
            category.setDisabled(false);
            categoryRepository.save(category);
            return  ResponseEntity.ok(category);
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Not modified");
    }
    public ResponseEntity<?> disableCategory(String name){
    Category category=categoryRepository.findAll().stream()
                 .filter(c->c.getCategoryName()!=null)
                .filter(c -> c.getCategoryName().equalsIgnoreCase(name))
                .findAny().orElse(null);
        if(category!=null){
            category.setDisabled(true);
            categoryRepository.save(category);
            return  ResponseEntity.ok(category);
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Not modified");
    }

    public Category categoryByName(String categoryName) {
        return categoryRepository.findAll().stream()
                .filter(c->c.getCategoryName()!=null)
                .filter(c -> c.getCategoryName().equalsIgnoreCase(categoryName))
                .findAny().orElse(null);
    }

    public Category updateCategory(UpdateCategoryRequest req) {
        Category cat = categoryRepository.findAll().stream()
                .filter(c->c.getCategoryName()!=null)
                .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getOldName()))
                .findAny().orElse(null);

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
    public ResponseEntity<?> saveCategory(CategoryRequest req) {
        try{
            Category cat = categoryRepository.findAll().stream()
                    .filter(c->c.getCategoryName()!=null)
                    .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getCategoryName()))
                    .findAny().orElse(null);
            if (cat==null) {

                Category category = new Category();
                if(!req.getCategoryName().equals(""))category.setCategoryName(req.getCategoryName());else throw new RuntimeException("category cannot be null");
                if(!req.getCategoryType().equals("")){category.setCategoryType(req.getCategoryType());}else throw new RuntimeException("category type cannot be null");
                if(!req.getDescription().equals(""))category.setDescription(req.getDescription()); else throw new RuntimeException("description cannot be null");
                if(!req.getPhoto().equals(""))category.setPhoto(req.getPhoto());else throw new RuntimeException("photo cannot be null");
                return ResponseEntity.ok(categoryRepository.save(category));

            }

        }catch (Exception ex){
           return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return null;
    }

    public ResponseEntity<?> saveSubCategory(SubCategoryRequest req) {
       try{
           SubCategory subCategory = new SubCategory();
           Category category=categoryRepository.findAll().stream()
                   .filter(c->c.getCategoryName()!=null)
                   .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getCategoryName()))
                   .findAny().orElse(null);
           if (category != null) {
               Optional<List<SubCategory>> subOp = Optional.ofNullable(category.getSubCategories());
               List<SubCategory> subCategories = subOp.orElse(new ArrayList<>());
               subCategory.setCategoryName(category.getCategoryName());
               if(!req.getSubCategoryName().equals("")) subCategory.setSubCategoryName(req.getSubCategoryName()); else throw new RuntimeException("Subcategory name cannot be null");
               if(!req.getDescription().equals(""))subCategory.setDescription(req.getDescription());
               if(!req.getPhoto().equals(""))subCategory.setPhotos(req.getPhoto());
               subCategories.add(subCategory);
               category.setSubCategories(subCategories);
               categoryRepository.save(category);
               return ResponseEntity.ok().body(subCategory);
           }
       } catch (Exception ex) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category not found");
       }
        return null;
    }

   public ResponseEntity<?> updateSubCategory(UpdateSubCategoryReq req){
        try {
            Category category = categoryRepository.findAll().stream()
                    .filter(c -> c.getCategoryName() != null)
                    .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getCategoryName()))
                    .findAny().orElseThrow(()->new RuntimeException("Category is null"));
            if (category != null) {
                List<SubCategory> subCategoryList = category.getSubCategories();
                SubCategory sub = subCategoryList.stream().filter(s -> s.getSubCategoryName().equalsIgnoreCase(req.getOldName())).findAny().orElse(null);
                if (sub != null) {
                    if (!req.getSubCategoryName().equals("")) sub.setSubCategoryName(req.getSubCategoryName());
                    else throw new RuntimeException("SubCategory name cannot be null");
                    if (!req.getDescription().equals("")) sub.setDescription(req.getDescription());
                    if (!req.getPhoto().equals("")) sub.setPhotos(req.getPhoto());
                    subCategoryList.remove(sub);
                    subCategoryList.add(sub);
                    category.setSubCategories(subCategoryList);
                    categoryRepository.save(category);
                    return new ResponseEntity<>(HttpStatus.OK);
                }

            }
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return null;
    }

    public ResponseEntity<?> enableSubCategory(UpdateSubCategoryReq req){
        Category category=categoryRepository.findAll().stream()
                .filter(c->c.getCategoryName()!=null)
                .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getCategoryName()))
                .findAny().orElse(null);
        if(category!=null){
            List<SubCategory> subCategoryList=category.getSubCategories();
            SubCategory sub=subCategoryList.stream().filter(s->s.getSubCategoryName().equalsIgnoreCase(req.getOldName())).findAny().orElse(null);
            if(sub!=null){
                sub.setDisabled(false);
                subCategoryList.remove(sub);
                subCategoryList.add(sub);
                category.setSubCategories(subCategoryList);
                categoryRepository.save(category);
                return new ResponseEntity<>(HttpStatus.OK);
            }

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category is null");
    }

    public ResponseEntity<?> disableSubCategory(UpdateSubCategoryReq req){
        Category category=categoryRepository.findAll().stream()
                .filter(c->c.getCategoryName()!=null)
                .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getCategoryName()))
                .findAny().orElse(null);
        if(category!=null){
            List<SubCategory> subCategoryList=category.getSubCategories();
            SubCategory sub=subCategoryList.stream().filter(s->s.getSubCategoryName().equalsIgnoreCase(req.getOldName())).findAny().orElse(null);
            if(sub!=null){
                sub.setDisabled(true);
                subCategoryList.remove(sub);
                subCategoryList.add(sub);
                category.setSubCategories(subCategoryList);
                categoryRepository.save(category);
                return new ResponseEntity<>(HttpStatus.OK);
            }

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category is null");
    }

    public ResponseEntity<?> getSubCategoryByName(UpdateSubCategoryReq req){
        Category category=categoryRepository.findAll().stream()
                .filter(c->c.getCategoryName()!=null)
                .filter(c -> c.getCategoryName().equalsIgnoreCase(req.getCategoryName()))
                .findAny().orElse(null);
        if(category!=null) {
            List<SubCategory> subCategoryList = category.getSubCategories();
            SubCategory sub = subCategoryList.stream().
                    filter(s -> s.getSubCategoryName() != null)
                    .filter(s -> s.getSubCategoryName().equalsIgnoreCase(req.getOldName())).findAny().orElse(null);
            if (sub != null) {
                return ResponseEntity.ok(sub);
            }
        }
        return  null;
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


    public Product getProductById(String id) {
        Optional<Product> prod = productsRepository.findById(id);
        return prod.orElse(null);

    }

    public List<Product> getAllAvailableProducts(){
        return productsRepository.findAll().stream()
                .filter(Product::isNotDisabled)
                .collect(Collectors.toList());
    }

}
