package com.prismhealth.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.*;
import com.prismhealth.repository.*;
import com.prismhealth.util.Actions;
import com.prismhealth.util.LogMessage;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;


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

    public ProductsService(VariantRepository variantRepository, CategoryRepository categoryRepository1,
                           SubCategoriesRepository subCategoriesRepository1, ProductsRepository productsRepository, PhotoRepository photoRepository, AccountRepository accountRepository, MailService mailService, NotificationRepo notificationRepo) {
        this.variantRepository = variantRepository;
        this.categoryRepository = categoryRepository1;
        this.subCategoriesRepository = subCategoriesRepository1;
        this.productsRepository = productsRepository;
        this.photoRepository = photoRepository;
        this.accountRepository = accountRepository;
        this.mailService = mailService;
        this.notificationRepo = notificationRepo;
    }
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public List<SubCategory> getSubcategoriesByName(String categoryName) {
        //TODO marshal up a response for when sub category does not exists
        return subCategoriesRepository.findAll()
                .stream().filter(r -> r.getCategory().equals(categoryName))
                .collect(Collectors.toList());
    }

    public List<SubCategory> getAllSubcategories() {
        //TODO marshal up a response for when sub category does not exists
        return new ArrayList<>(subCategoriesRepository.findAll());
    }

    public List<Product> getAllProducts(String subCategoryName) {
        //TODO marshal up a response for when products do not exists
        List<Product> products = productsRepository.findAll()
                .stream().filter(r -> r.getSubCategory().equals(subCategoryName))
                .collect(Collectors.toList());
        for (Product product: products){
            product.setUsers(accountRepository.findOneByPhone(product.getUser()));
        }
        return products;
    }

    /* getting product subCategory and category by name*/

    public List<Product> productByName(String productName) {
        //TODO marshal up a response for when product does not exists
        return productsRepository.findAll()
                .stream().filter(r -> r.getProductName().contains(productName))
                .collect(Collectors.toList());
    }

    public List<SubCategory> subCategoryByName(String subCategoryName) {
        //TODO marshal up a response for when product does not exists
        return subCategoriesRepository.findAll()
                .stream().filter(r -> r.getSubCategoryName().contains(subCategoryName))
                .collect(Collectors.toList());
    }

    public List<Category> categoryByName(String categoryName) {
        //TODO marshal up a response for when category does not exists
        return categoryRepository.findAll()
                .stream().filter(r -> r.getCategoryName().contains(categoryName))
                .collect(Collectors.toList());
    }

    public List<Variant> variantByName(String variantName) {
        return variantRepository.findAll()
                .stream().filter(variant -> variant.getVariantName().contains(variantName)).collect(Collectors.toList());
    }

    /* saving category,subCategory and product*/
    public Category saveCategory(String category,MultipartFile multipartFile) {

        try {
            Category category1 = new ObjectMapper().readValue(category,Category.class);
            if (categoryByName(category1.getCategoryName()).isEmpty()) {
                Photos photos = new Photos();
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                try {
                    photos.setPhoto(new Binary(BsonBinarySubType.BINARY, multipartFile.getBytes()));
                    category1.setPhotos(photoRepository.save(photos).getId());
                    //category1.setPhotos(fileName);
                    //String uploadDir = "user-photos/" + category1.getCategoryName();
                    //saveFile(uploadDir, fileName, multipartFile);
                    return categoryRepository.save(category1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //TODO marshal up a response for when category exists
        return null;
    }

    public SubCategory saveSubCategory(String subCategory1,MultipartFile multipartFile) {

        try {
            SubCategory subCategory = new ObjectMapper().readValue(subCategory1, SubCategory.class);
            if (!categoryByName(subCategory.getCategory()).isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                Photos photos = new Photos();
                try {
                    photos.setPhoto(new Binary(BsonBinarySubType.BINARY, multipartFile.getBytes()));
                    subCategory.setPhotos(photoRepository.save(photos).getId());

                    //String uploadDir = "user-photos/" + subCategory.getSubCategoryName();
                    //saveFile(uploadDir, fileName, multipartFile);
                    return subCategoriesRepository.save(subCategory);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //TODO marshal up a response for when category does not exists
        return null;
    }

    public Product saveProduct(Product product, MultipartFile multipartFile, Principal principal) {

        Users users = accountRepository.findOneByPhone(principal.getName());
        Variant variant = new Variant();
        variant.setVariantName(product.getProductVariant());
        variant.setSubCategory(product.getSubCategory());

        if (!subCategoryByName(product.getSubCategory()).isEmpty()) {
            if (variantByName(product.getProductVariant()).isEmpty())
                variantRepository.save(variant);
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            //String uploadDir = "user-photos/" + users.getPhone();

            //saveFile(uploadDir, fileName, multipartFile);
            try {
             Photos photos = new Photos();
            photos.setPhoto(new Binary(BsonBinarySubType.BINARY, multipartFile.getBytes()));
            product.setPhotos(photoRepository.save(photos).getId());
            product.setUser(users.getPhone());
            sendEmail(users,"createProduct");
            return productsRepository.save(product);
             } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //TODO marshal up a response for when subCategory does not exists
        return null;
    }

    public Photos getPhoto(String id) {
        return photoRepository.findById(id).get();
    }

    public Variant createVariant(Variant variant) {
        if (!subCategoryByName(variant.getSubCategory()).isEmpty()) {
            return variantRepository.save(variant);
        }
        //TODO marshal up a response for when subCategory does not exists
        return null;
    }


    public ResponseEntity<?> deleteProduct(Product product) {
        List<?> products = productsRepository.findAll().stream().filter(
                product1 -> product1.getProductName()
                        .equals(product.getProductName()) &&
                        product1.getSubCategory().equals(product.getSubCategory())).collect(Collectors.toList());

        if (products.isEmpty()) {
            try {
                throw new ChangeSetPersister.NotFoundException();
            } catch (ChangeSetPersister.NotFoundException e) {
                e.printStackTrace();
            }
        }
        productsRepository.delete(product);
        return ResponseEntity.ok().body(product.getProductName() + " Successfully deleted");
    }

    public ResponseEntity<?> getImage(String productId) {
        List<Product> product1 = productsRepository.findAll().stream()
                .filter(product -> product.getProductName().contains(productId)).collect(Collectors.toList());
        List<String> list = new ArrayList<>();
        for (int i = 0; i < product1.size(); i++) {
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("")
                    .toUriString();
            list.add(fileDownloadUri);
        }

        return ResponseEntity.ok().body(list);

    }

    public ResponseEntity<?> updateProduct(Product product) {
        List<?> products = productsRepository.findAll().stream().filter(
                product1 -> product1.getProductName()
                        .equals(product.getProductName()) &&
                        product1.getSubCategory().equals(product.getSubCategory())).collect(Collectors.toList());
        if (products.isEmpty()) {
            try {
                throw new ChangeSetPersister.NotFoundException();
            } catch (ChangeSetPersister.NotFoundException e) {
                e.printStackTrace();
            }
        }

        productsRepository.save(product);
        return ResponseEntity.ok().body(product.getProductName() + " Successfully deleted");
    }
    public String sendEmail(Users users, String action){

        if (users==null){
            return "User with phone number not found";
        }
        String message = null;
        if (action.equals("createAccount")){
            message = "Account successfully created for "+ users.getPhone();
        }else if (action.equals("createProduct")){
            message = "Product successfully created by "+ users.getPhone()+" "+users.getEmail();
        }else if (action.equals("createService")){
            message = "Service successfully created by "+ users.getPhone()+" "+users.getEmail();
        }else if (action.equals("createBooking")){
            message = "Booking successfully created by "+ users.getPhone()+" "+users.getEmail();
        }else if (action.equals("notifyProvider")){
            message = "Product booking made for your product";
        }

        if (users!=null) {
            log.info(message);
            Mail mail = new Mail();
            mail.setMailFrom("prismhealth@gmail.com");
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
                return "Notification sent to : " + users.getEmail();

        } else {
            log.info("Sending notification  " + LogMessage.FAILED + " User does not exist");
            return null;
        }

    }
}
