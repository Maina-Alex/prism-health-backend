package com.prismhealth.services;

import java.security.Principal;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import com.prismhealth.Models.*;
import com.prismhealth.config.Constants;
import com.prismhealth.repository.*;
import com.prismhealth.util.Actions;
import com.prismhealth.util.LogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final Logger log = LoggerFactory.getLogger(BookingService.class);
    @Autowired
    private BookingsRepo bookingsRepo;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private NotificationRepo notificationRepo;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private ServiceRepo serviceRepo;

    public Map<String, List<ServiceBooking>> getServiceBookings(String serviceId) {
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(30);
        log.info("Getting bookings for service " + serviceId);
        List<ServiceBooking> bookings = new ArrayList<>();
        while (today.compareTo(future) <= 0) {

            int hour = 6;

            while (hour > 5 && hour < 17) {
                ServiceBooking b = new ServiceBooking();
                List<Bookings> serviceB = bookingsRepo.findAllByServiceIdAndDateAndHour(serviceId, today.toString(),
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
        if (optional.isPresent())
            bookings.forEach(b -> {
                if (!b.getServiceId().isEmpty()
                        && !bookingsRepo.existsByServiceIdAndDateAndHour(b.getServiceId(), b.getDate(), b.getHour())) {

                    b.setUserId(optional.get().getPhone());
                    b.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                    bookingsRepo.save(b);
                    sendEmail(optional.get(),serviceRepo.findById(b.getServiceId()).get(), "notifyProvider");
                }

            });

        return this.getServiceBookings(bookings.get(0).getServiceId());
    }

    public Map<String, List<Bookings>> cancelBookings(String id, Principal principal) {
        Optional<Users> optional = Optional.ofNullable(accountRepository.findOneByPhone(principal.getName()));
        if (optional.isPresent()) {
            Optional<Bookings> bOptional = bookingsRepo.findById(id);

            if (bOptional.isPresent()) {
                Bookings b = bOptional.get();

                log.info("cancel booking for service " + b.getServiceId());

                b.setCancelled(true);
                bookingsRepo.save(b);
                Optional<Services> sOptional = serviceRepo.findById(b.getServiceId());
                if (sOptional.isPresent())

                    sendEmail(optional.get(), sOptional.get(), "cancelled");
            }else {
                log.error("booking with id "+id+" does not exist");
            }

        }
        return this.getBookingsHistory(principal);
    }

    public Map<String, List<Bookings>> getBookingsHistory(Principal principal) {
        Users optional = accountRepository.findOneByPhone(principal.getName());

        if (optional.getAccountType().equals("PROVIDER")) {

            List<Bookings> bookings = new ArrayList<>();
            List<Services> services = serviceRepo.findAllByProviderId(optional.getPhone());
            services.forEach(s -> bookings
                    .addAll(bookingsRepo.findAllByServiceId(s.getId(), Sort.by("timestamp").descending())));

            return bookings.stream().map(b -> {
                Optional<Users> u = accountRepository.findById(b.getUserId());
                b.setUser(u.orElse(null));
                return b;
            }).collect(Collectors.groupingBy(Bookings::getServiceId));

        } else {

            Map<String, List<Bookings>> bookings = bookingsRepo
                    .findAllByUserId(optional.getPhone(), Sort.by("date").descending()).stream()
                    .collect(Collectors.groupingBy(Bookings::getServiceId));

            return bookings;
        }

    }

    @Async
    public void sendEmail(Users users, Services services, String action) {
            if (users == null) {
                log.info("User with phone number not found");
            }
            String message = null;

            if (action.equals("create")) {
                message = "Booking for service, " + services.getName() + " made successfully for " + users.getEmail();
            } else if (action.equals("cancelled")) {
                message = "Booking for service " + services.getName() + " cancelled successfully";
            }else if (action.equals("notifyProvider")){
                message = "";
            }

            if (users != null) {
                log.info(message);
                AccountDetails details = new AccountDetails();
                details.setEmail(users.getEmail());
                details.setAccesstoken(users.getDeviceToken());
                details.setUsername(users.getPhone());

                Notification notification = new Notification();
                notification.setEmail(users.getEmail());
                notification.setUserId(users.getPhone());
                notification.setMessage(message);
                notification.setAction(null);
                notification.setDetails(details);
                notification.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                notificationRepo.save(notification);
                log.info("Sent notification to : " + users.getEmail() + " " + LogMessage.SUCCESS);
                Mail mail = new Mail();
                mail.setMailFrom(Constants.email);
                mail.setMailTo(users.getEmail());
                mail.setMailSubject("Prism-health Notification services");
                mail.setMailContent("You have successfully created booking for service "+services.getName()+ " at "+services.getTimestamp());
                Mail providerMail = new Mail();

                providerMail.setMailFrom(Constants.email);
                providerMail.setMailTo(accountRepository
                        .findOneByPhone(serviceRepo.findById(services.getId()).get().getProviderId()).getEmail());
                providerMail.setMailSubject("Prism-health Notification services");
                providerMail.setMailContent("You have a new booking for service "+services.getName()+ " at "+services.getTimestamp());

                mailService.sendEmail(mail);


            } else {
                log.info("Sending notification  " + LogMessage.FAILED + " User does not exist");

            }
    }
}
