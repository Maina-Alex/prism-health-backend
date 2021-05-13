package com.prismhealth.repository;

import com.prismhealth.Models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface AccountRepository extends MongoRepository<User,String> {
    User findOneByPhone(String phone);

    Optional<User> findOneByEmail(String s);

    List<User> findByPositionNear(Point location, Distance distance);

    List<User> findByBlocked(boolean blocked, Sort sort);

    List<User> findByVerified(boolean verified);

    List<User> findByDeletedAndApproveDelete(boolean deleted, boolean approveDelete, Sort sort);

    List<User> findByapproveDelete(boolean approveDelete, Sort sort);

    List<User> findByVerifiedOnBetween(Date from, Date to);
}
