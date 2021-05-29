package com.prismhealth.repository;

import com.prismhealth.Models.Category;
import com.prismhealth.Models.PostCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PostCategoryRepository extends MongoRepository<PostCategory, String> {
    Optional<Category> findByPostCategoryName(String categoryName);
}
