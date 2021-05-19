package com.prismhealth.repository;

import com.prismhealth.Models.Photos;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepository extends MongoRepository<Photos,String > {
}
