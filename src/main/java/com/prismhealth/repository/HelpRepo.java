package com.prismhealth.repository;

import java.util.List;


import com.prismhealth.Models.Help;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HelpRepo extends MongoRepository<Help, String> {
    List<Help> findAllByUserId(String userId, Sort sort);

    List<Help> findAllByIssueId(String issueId, Sort sort);

}
