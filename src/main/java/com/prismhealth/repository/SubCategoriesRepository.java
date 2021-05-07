package com.prismhealth.repository;

import com.prismhealth.Models.SubCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubCategoriesRepository extends MongoRepository<SubCategory, String> {
}
