package com.prismhealth.repository;

import com.prismhealth.Models.UserRoles;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRolesRepo extends MongoRepository<UserRoles, String> {
    List<UserRoles> findAllByUserId(String phone);
}
