package com.prismhealth.repository;

import com.prismhealth.Models.UserRating;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRatingsRepo extends MongoRepository<UserRating, String> {

    List<UserRating> findAllByUserId(String userId, Sort sort);

}