package com.prismhealth.repository;

import com.prismhealth.Models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductsRepository extends MongoRepository<Product, String> {

}
