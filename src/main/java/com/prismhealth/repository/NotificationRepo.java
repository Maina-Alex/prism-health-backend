package com.prismhealth.repository;

import com.prismhealth.Models.Notification;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepo extends MongoRepository<Notification, String> {
    List<Notification> findAllByUserId(String userId, Sort sort);

}
