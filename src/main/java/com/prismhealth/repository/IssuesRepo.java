package com.prismhealth.repository;

import java.util.List;


import com.prismhealth.Models.Issues;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IssuesRepo extends MongoRepository<Issues, String> {
    List<Issues> findByUserIdAndStatus(String userId, String status, Sort sort);

}
