package com.prismhealth.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.prismhealth.Models.Bookings;
import com.prismhealth.Models.ServiceBooking;
import com.prismhealth.repository.BookingsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ServiceBookingService {
    private final Logger log = LoggerFactory.getLogger(ServiceBookingService.class);
    @Autowired
    private BookingsRepo bookingsRepo;

    public List<ServiceBooking> getServiceBookings(String serviceId) {
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(31);

        log.info("Getting bookings for service " + serviceId);

        List<ServiceBooking> bookings = new ArrayList<>();
        while (today.compareTo(future) <= 0) {
            ServiceBooking b = new ServiceBooking();

            List<Bookings> serviceB = bookingsRepo.findByServiceIdAndDate(serviceId, Date.valueOf(today),
                    Sort.by("timestamp").descending());
            if (serviceB.isEmpty()) {
                b.setAvailable(true);

            } else {
                if (serviceB.get(0).isCancelled())
                    b.setAvailable(true);
                else
                    b.setAvailable(false);
            }
            b.setDay(today.toString());

            bookings.add(b);
            today = today.plusDays(1);

        }
        return bookings;

    }

}
