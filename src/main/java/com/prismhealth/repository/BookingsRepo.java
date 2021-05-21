package com.prismhealth.repository;

import java.util.Date;
import java.util.List;

import com.prismhealth.Models.Bookings;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingsRepo extends MongoRepository<Bookings, String> {
    boolean existsByServiceIdAndDateAndHour(String serviceId, Date date, int hour);

    List<Bookings> findAllByServiceIdAndDateAndHour(String serviceId, Date date, int hour);

    List<Bookings> findAllByServiceId(String serviceId, Sort sort);

}
