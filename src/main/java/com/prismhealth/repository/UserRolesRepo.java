package com.prismhealth.repository;

import com.prismhealth.Models.UserRoles;
import com.prismhealth.Models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRolesRepo extends MongoRepository<UserRoles, String> {
    public List<UserRoles> findAllByUserId(String phone);
}
