package com.prismhealth.repository;

import com.prismhealth.Models.Variant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VariantRepository extends MongoRepository<Variant, String> {
}
