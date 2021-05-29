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
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;


import com.prismhealth.Models.*;
import com.prismhealth.repository.*;
import com.prismhealth.util.Actions;
import com.prismhealth.util.LogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final Logger log = LoggerFactory.getLogger(BookingService.class);
    @Autowired
    private BookingsRepo bookingsRepo;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    MailService mailService;
    @Autowired
    NotificationRepo notificationRepo;
    @Autowired
    ExecutorService executor;
    @Autowired
    ServiceRepo serviceRepo;

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
                    log.info("create this booking "+b.getServiceId());
                    b.setUserId(optional.get().getPhone());
                    b.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                    bookingsRepo.save(b);
                }

            });
            sendEmail(optional.get(),bookings,"create");

        }
        return this.getServiceBookings(bookings.get(0).getServiceId());
    }
    public Map<String, List<ServiceBooking>> cancelBookings(List<Bookings> bookings, Principal principal) {
        Optional<Users> optional = accountRepository.findById(principal.getName());
        if (optional.isPresent()) {
            bookings.forEach(b -> {
                if (!bookingsRepo.existsByServiceIdAndDateAndHour(b.getServiceId(), b.getDate(), b.getHour())) {
                    log.info("create this booking "+b.getServiceId());
                    b.setUserId(optional.get().getPhone());
                    b.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                    b.isCancelled();
                    bookingsRepo.save(b);
                }

            });
            sendEmail(optional.get(),bookings,"cancelled");

        }
        return this.getServiceBookings(bookings.get(0).getServiceId());
    }

    public Map<String, List<Bookings>> getBookingsHistory(Principal principal) {
        Optional<Users> optional = accountRepository.findById(principal.getName());
        if (optional.isPresent()) {
            Map<String, List<Bookings>> bookings = bookingsRepo
                    .findAllByUserId(optional.get().getPhone(), Sort.by("date").descending()).stream()
                    .collect(Collectors.groupingBy(Bookings::getServiceId));

            return bookings;
        }
        return null;

    }
    public void sendEmail(Users users, List<Bookings> bookings,String action) {
        Runnable task = () -> {
            if (users == null) {
                log.info("User with phone number not found");
            }
            String message = null;

            if (action.equals("create")) {
                message = "Booking for service \n" + bookings + " made successfully for " + users.getEmail();
            }else if (action.equals("cancelled")){
                message = String.format("Booking for service \n%s cancelled successfully for %s",bookings, users.getEmail());
            }

            if (users != null) {
                log.info(message);
                Mail mail = new Mail();
                mail.setMailFrom("prismhealth658@gmail.com");
                mail.setMailTo(users.getEmail());
                mail.setMailSubject("Prism-health Notification services");
                mail.setMailContent(message);
                Mail providerMail = new Mail();
                for (Bookings bookings1: bookings){
                providerMail.setMailFrom("prismhealth658@gmail.com");
                providerMail.setMailTo(accountRepository.findOneByPhone(serviceRepo.findById(bookings1.getServiceId()).get().getProviderId()).getEmail() );
                providerMail.setMailSubject("Prism-health Notification services");
                providerMail.setMailContent(message);

                mailService.sendEmail(mail);
                }
                Notification notification = new Notification();
                notification.setEmail(users.getEmail());
                notification.setUserId(users.getPhone());
                notification.setMessage(message);
                notification.setAction(Actions.RESET_PASSSWORD);
                notification.setTimestamp(java.util.Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                notificationRepo.save(notification);
                log.info("Sent notification to : " + users.getEmail() + " " + LogMessage.SUCCESS);

            } else {
                log.info("Sending notification  " + LogMessage.FAILED + " User does not exist");

            }

        };

        executor.submit(task);

    }
}
