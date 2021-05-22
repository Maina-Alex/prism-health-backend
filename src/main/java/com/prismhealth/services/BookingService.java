package com.prismhealth.services;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.prismhealth.Models.Bookings;
import com.prismhealth.Models.ServiceBooking;
import com.prismhealth.Models.Users;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.BookingsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final Logger log = LoggerFactory.getLogger(BookingService.class);
    @Autowired
    private BookingsRepo bookingsRepo;
    @Autowired
    AccountRepository accountRepository;

    public Map<String, List<ServiceBooking>> getServiceBookings(String serviceId) {
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(7);
        log.info("Getting bookings for service " + serviceId);
        List<ServiceBooking> bookings = new ArrayList<>();
        while (today.compareTo(future) <= 0) {

            int hour = 6;

            while (hour > 5 && hour < 17) {
                ServiceBooking b = new ServiceBooking();
                List<Bookings> serviceB = bookingsRepo.findAllByServiceIdAndDateAndHour(serviceId, Date.valueOf(today),
                        hour);
                if (serviceB.isEmpty()) {
                    b.setAvailable(true);

                } else {
                    if (serviceB.get(0).isCancelled())
                        b.setAvailable(true);
                    else
                        b.setAvailable(false);
                }
                if (LocalDate.now().compareTo(today) == 0 && hour < LocalDateTime.now().getHour()) {
                    b.setAvailable(false);

                }

                b.setDay(today.toString());
                b.setHour(hour);
                bookings.add(b);
                hour = hour + 1;

            }
            today = today.plusDays(1);

        }

        Map<String, List<ServiceBooking>> services = bookings.stream()
                .collect(Collectors.groupingBy(ServiceBooking::getDay));

        return services;

    }

    public Map<String, List<ServiceBooking>> createBookings(List<Bookings> bookings, Principal principal) {
        Optional<Users> optional = accountRepository.findById(principal.getName());
        if (optional.isPresent()) {
            bookings.forEach(b -> {
                if (!bookingsRepo.existsByServiceIdAndDateAndHour(b.getServiceId(), b.getDate(), b.getHour())) {

                    b.setUserId(optional.get().getPhone());
                    b.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                    bookingsRepo.save(b);
                }

            });

        }
        return this.getServiceBookings(bookings.get(0).getServiceId());
    }
}
