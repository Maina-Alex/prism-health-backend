package com.prismhealth.repository;

import com.prismhealth.Models.Users;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<Users, String> {
    Users findOneByPhone(String phone);

    boolean existsByPhone(String phone);

    Optional<Users> findOneByEmail(String s);

    List<Users> findByPositionNear(Point location, Distance distance);

    List<Users> findByBlocked(boolean blocked, Sort sort);

    List<Users> findByVerified(boolean verified);

    List<Users> findByDeletedAndApproveDelete(boolean deleted, boolean approveDelete, Sort sort);

    List<Users> findByapproveDelete(boolean approveDelete, Sort sort);

    List<Users> findByVerifiedOnBetween(Date from, Date to);
}
