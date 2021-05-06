package com.prismhealth.repository;

import com.prismhealth.Models.UserRoles;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface UserRolesRepo extends MongoRepository<UserRoles,String> {
    public List<UserRoles> findAllByUserId(String phone);
}
