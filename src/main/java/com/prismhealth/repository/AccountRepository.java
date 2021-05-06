package com.prismhealth.repository;

import com.prismhealth.Models.User;
import com.prismhealth.dto.Request.SignInRequest;
import com.prismhealth.dto.Request.SignUpRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface AccountRepository extends MongoRepository<User,String> {
    public Optional<User> findOneByPhone(String phone);
}
