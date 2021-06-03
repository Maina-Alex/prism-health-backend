package com.prismhealth.repository;

import com.prismhealth.Models.Bookings;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingsRepo extends MongoRepository<Bookings, String> {
    boolean existsByServiceIdAndDateAndHour(String serviceId, String date, int hour);

    List<Bookings> findAllByServiceIdAndDateAndHour(String serviceId, String date, int hour);

    List<Bookings> findAllByServiceId(String serviceId, Sort sort);

    List<Bookings> findAllByUserPhone(String userId, Sort sort);

}
