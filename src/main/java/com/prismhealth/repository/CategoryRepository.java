package com.prismhealth.repository;

import java.util.Optional;

import com.prismhealth.Models.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByCategoryName(String categoryName);
}
