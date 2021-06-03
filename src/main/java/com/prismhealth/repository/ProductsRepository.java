package com.prismhealth.repository;

import java.util.List;

import com.prismhealth.Models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductsRepository extends MongoRepository<Product, String> {
    List<Product> findAllByUser(String user);

}
