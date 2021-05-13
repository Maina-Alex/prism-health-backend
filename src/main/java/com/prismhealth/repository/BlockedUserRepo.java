package com.prismhealth.repository;

import com.prismhealth.Models.BlockedUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockedUserRepo extends MongoRepository<BlockedUser, String> {

}
