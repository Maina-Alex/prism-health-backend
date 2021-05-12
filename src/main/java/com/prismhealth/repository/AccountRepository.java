package com.prismhealth.repository;

import com.prismhealth.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface AccountRepository extends MongoRepository<User,String> {
    User findOneByPhone(String phone);

    Optional<User> findOneByEmail(String s);
}
