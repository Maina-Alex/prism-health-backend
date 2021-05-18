package com.prismhealth.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.prismhealth.Models.Bookings;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingsRepo extends MongoRepository<Bookings, String> {
    boolean existsByProviderIdAndDate(String serviceId, Date date);

    List<Bookings> findAllByProviderId(String providerId, Sort sort);

    List<Bookings> findAllByServiceId(String serviceId);

    List<Bookings> findByServiceIdAndDate(String serviceId, Date date, Sort sort);
}
