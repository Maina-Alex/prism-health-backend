package com.prismhealth.services;

import com.prismhealth.Models.*;
import com.prismhealth.repository.*;
import com.prismhealth.util.Actions;
import com.prismhealth.util.LogMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class ProductsService {
    private final VariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoriesRepository subCategoriesRepository;
    private final ProductsRepository productsRepository;
    private final PhotoRepository photoRepository;
    private final AccountRepository accountRepository;
    private final MailService mailService;
    private final NotificationRepo notificationRepo;

    private ExecutorService executor;

    public ProductsService(ExecutorService executor, VariantRepository variantRepository,
            CategoryRepository categoryRepository1, SubCategoriesRepository subCategoriesRepository1,
            ProductsRepository productsRepository, PhotoRepository photoRepository, AccountRepository accountRepository,
            MailService mailService, NotificationRepo notificationRepo) {
        this.variantRepository = variantRepository;
        this.categoryRepository = categoryRepository1;
        this.subCategoriesRepository = subCategoriesRepository1;
        this.productsRepository = productsRepository;
        this.photoRepository = photoRepository;
        this.accountRepository = accountRepository;
        this.mailService = mailService;
        this.notificationRepo = notificationRepo;
        this.executor = executor;
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<SubCategory> getSubcategoriesByName(String categoryName) {
        // TODO marshal up a response for when sub category does not exists
        return subCategoriesRepository.findAll().stream().filter(r -> r.getCategory().equalsIgnoreCase(categoryName))
                .collect(Collectors.toList());
    }

    public List<SubCategory> getAllSubcategories() {
        // TODO marshal up a response for when sub category does not exists
        return new ArrayList<>(subCategoriesRepository.findAll());
    }

    public List<Product> getAllProducts(String subCategoryName) {
        // TODO marshal up a response for when products do not exists
        List<Product> products = productsRepository.findAll().stream()
                .filter(r -> r.getSubCategory().equalsIgnoreCase(subCategoryName)).collect(Collectors.toList());
        for (Product product : products) {
            product.setUsers(accountRepository.findOneByPhone(product.getUser()));
        }
        return products;
    }

    /* getting product subCategory and category by name */

    public List<Product> productByName(String productName) {
        // TODO marshal up a response for when product does not exists
        List<Product> products = productsRepository.findAll().stream()
                .filter(r -> r.getProductName().contains(productName)).collect(Collectors.toList());
        for (Product product : products) {
            product.setUsers(accountRepository.findOneByPhone(product.getUser()));
        }
        return products;
    }

    public List<SubCategory> subCategoryByName(String subCategoryName) {
        // TODO marshal up a response for when product does not exists
        return subCategoriesRepository.findAll().stream().filter(r -> r.getSubCategoryName().contains(subCategoryName))
                .collect(Collectors.toList());
    }

    public List<Category> categoryByName(String categoryName) {
        // TODO marshal up a response for when category does not exists
        return categoryRepository.findAll().stream().filter(r -> r.getCategoryName().contains(categoryName))
                .collect(Collectors.toList());
    }

    public List<Variant> variantByName(String variantName) {
        return variantRepository.findAll().stream().filter(variant -> variant.getVariantName().contains(variantName))
                .collect(Collectors.toList());
    }

    /* saving category,subCategory and product */
    public Category saveCategory(Category category) {
        Optional<Category> cat = categoryRepository.findByCategoryName(category.getCategoryName());

        if (!cat.isPresent()) {

            return categoryRepository.save(category);

        }

        // TODO marshal up a response for when category exists
        return null;
    }

    public SubCategory saveSubCategory(SubCategory subCategory) {

        return subCategoriesRepository.save(subCategory);

    }

    public Product saveProduct(Product product, Principal principal) {

        Users users = accountRepository.findOneByPhone(principal.getName());
        if (users!=null){
        Variant variant = new Variant();
        variant.setVariantName(product.getProductVariant());
        variant.setSubCategory(product.getSubCategory());
        Users users1 = accountRepository.findOneByPhone(product.getUser());
        if (!subCategoryByName(product.getSubCategory()).isEmpty()) {
            if (variantByName(product.getProductVariant()).isEmpty())
                variantRepository.save(variant);
            if (product.getPosition().length<2)
                product.setPosition(new double[]{users1.getPosition()[0],users1.getPosition()[1]});
            sendEmail(users, "createProduct");
            product.setUser(users.getPhone());
            Product product1 = productsRepository.save(product);
            product1.setUsers(users1);
            return product1;
        }
        return null;

        }
        // TODO marshal up a response for when subCategory does not exists
        return null;
    }

    public Photos getPhoto(String id) {
        return photoRepository.findById(id).get();
    }

    public Variant createVariant(Variant variant) {
        if (!subCategoryByName(variant.getSubCategory()).isEmpty()) {
            return variantRepository.save(variant);
        }
        // TODO marshal up a response for when subCategory does not exists
        return null;
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

    public void sendEmail(Users users, String action) {
        Runnable task = () -> {
            if (users == null) {
                log.info("User with phone number not found");
            }
            String message = null;
            if (action.equals("createAccount")) {
                message = "Account successfully created for " + users.getPhone();
            } else if (action.equals("createProduct")) {
                message = "Product successfully created by " + users.getPhone() + " " + users.getEmail();
            } else if (action.equals("createService")) {
                message = "Service successfully created by " + users.getPhone() + " " + users.getEmail();
            } else if (action.equals("createBooking")) {
                message = "Booking successfully created by " + users.getPhone() + " " + users.getEmail();
            } else if (action.equals("notifyProvider")) {
                message = "Product booking made for your product";
            }

            if (users != null) {
                log.info(message);
                Mail mail = new Mail();
                mail.setMailFrom("prismhealth658@gmail.com");
                mail.setMailTo(users.getEmail());
                mail.setMailSubject("Prism-health Notification services");
                mail.setMailContent(message);

                mailService.sendEmail(mail);
                Notification notification = new Notification();
                notification.setEmail(users.getEmail());
                notification.setUserId(users.getPhone());
                notification.setMessage(message);
                notification.setAction(Actions.RESET_PASSSWORD);
                notification.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                notificationRepo.save(notification);
                log.info("Sent notification to : " + users.getEmail() + " " + LogMessage.SUCCESS);

            } else {
                log.info("Sending notification  " + LogMessage.FAILED + " User does not exist");

            }

        };

        executor.submit(task);

    }

    public List<Product> getAllAvailableProducts() {
        return productsRepository.findAll();
    }
    public List<Product> getProductsByProviderId(String providerId){
        return productsRepository.findAll().stream()
                .filter(product -> product.getUser()==providerId)
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> deleteCategory(String categoryName) {
         categoryRepository.delete(categoryRepository.findByCategoryName(categoryName).get());
        return ResponseEntity.ok().body("Successfully deleted..");
    }

    public ResponseEntity<?> deleteSubCategory(String subCategoryName) {
        subCategoriesRepository.deleteAll(subCategoriesRepository.findAll()
                .stream().filter(subCategory -> subCategory.getSubCategoryName()==subCategoryName).collect(Collectors.toList()));
        return ResponseEntity.ok("Successfully deleted");
    }
}
