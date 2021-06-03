package com.prismhealth.services;

import java.security.Principal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import com.prismhealth.Models.*;
import com.prismhealth.config.Constants;
import com.prismhealth.Models.ServiceBooking;
import com.prismhealth.repository.*;
import com.prismhealth.util.LogMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookingService {
    private final Logger log = LoggerFactory.getLogger(BookingService.class);
    private final UserRepository userRepository;
    private final MailService mailService;
    private final ServiceRepo serviceRepo;
    private final BookingsRepo bookingsRepo;
    private final ExecutorService executor;

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

        return new TreeMap<String, List<ServiceBooking>>(services);

    }

    public Map<String, List<ServiceBooking>> createBookings(List<Bookings> bookings, Principal principal) {
        Users users = userRepository.findByPhone(principal.getName());

        bookings.forEach(b -> {
            if (!b.getServiceId().isEmpty()
                    && !bookingsRepo.existsByServiceIdAndDateAndHour(b.getServiceId(), b.getDate(), b.getHour())) {
                b.setUserPhone(users.getPhone());
                b.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                bookingsRepo.save(b);
                sendEmail(users, serviceRepo.findById(b.getServiceId()).get(), "notifyProvider");
            }

        });

        return this.getServiceBookings(bookings.get(0).getServiceId());
    }

    public Map<String, List<Bookings>> cancelBookings(String id, Principal principal) {
        Optional<Users> optional = Optional.ofNullable(userRepository.findByPhone(principal.getName()));
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
            } else {
                log.error("booking with id " + id + " does not exist");
            }

        }
        return this.getBookingsHistory(principal);
    }

    public Map<String, List<Bookings>> getBookingsHistory(Principal principal) {
        Users optional = userRepository.findByPhone(principal.getName());

        if (optional.getAccountType().equals("PROVIDER")) {
            List<Bookings> bookings = new ArrayList<>();
            List<Services> services = serviceRepo.findAllByProviderPhone(optional.getPhone());
            services.forEach(s -> bookings
                    .addAll(bookingsRepo.findAllByServiceId(s.getId(), Sort.by("timestamp").descending())));

            return bookings.stream().peek(b -> {
                Optional<Users> u = Optional.ofNullable(userRepository.findByPhone(b.getUserPhone()));
                b.setUser(u.orElse(null));
                b.setService(serviceRepo.findById(b.getServiceId()).get());
            }).collect(Collectors.groupingBy(Bookings::getServiceId));

        } else {

            return bookingsRepo.findAllByUserId(optional.getPhone(), Sort.by("date").descending()).stream().map(b -> {
                b.setService(serviceRepo.findById(b.getServiceId()).get());
                return b;
            }).collect(Collectors.groupingBy(Bookings::getServiceId));
        }

    }

    public void sendEmail(Users users, Services services, String action) {
        Runnable task = () -> {

            if (users == null) {
                log.info("User with phone number not found");
            }
            String message = null;

            if (action.equals("create")) {
                message = "Booking for service, " + services.getName() + " made successfully for " + users.getEmail();
            } else if (action.equals("cancelled")) {
                message = "Booking for service " + services.getName() + " cancelled successfully";
            } else if (action.equals("notifyProvider")) {
                message = "";
            }

            if (users != null) {
                log.info(message);
                AccountDetails details = new AccountDetails();
                details.setEmail(users.getEmail());
                details.setAccesstoken(users.getVerificationToken());
                details.setUsername(users.getPhone());

                Notice notice = new Notice();
                notice.setEmail(users.getEmail());
                notice.setUserId(users.getPhone());
                notice.setMessage(message);
                notice.setAction(null);
                notice.setDetails(details);

                Notifications notifications = Optional.ofNullable(users.getNotifications()).orElse(new Notifications());
                List<Notice> noticeList = Optional.ofNullable(notifications.getNotices())
                        .orElse(new ArrayList<Notice>());
                notifications.setNotices(noticeList);
                noticeList.add(notice);
                users.setNotifications(notifications);
                users.getNotifications().setNotices(noticeList);
                userRepository.save(users);
                log.info("Sent notices to : " + users.getEmail() + " " + LogMessage.SUCCESS);
                if (users.getEmail() != null && !users.getEmail().equals("")) {
                    Mail mail = new Mail();
                    mail.setMailFrom(Constants.email);
                    mail.setMailTo(users.getEmail());
                    mail.setMailSubject("Prism-health Notice services");
                    mail.setMailContent("You have successfully created booking for service " + services.getName()
                            + " at " + services.getTimestamp());
                    mailService.sendEmail(mail);
                }
                String email = userRepository.findByPhone(services.getProviderPhone()).getEmail();
                Mail providerMail = new Mail();
                providerMail.setMailFrom(Constants.email);
                providerMail.setMailTo(email);
                providerMail.setMailSubject("Prism-health Notice services");
                providerMail.setMailContent(
                        "You have a new booking for service " + services.getName() + " at " + services.getTimestamp());
                mailService.sendEmail(providerMail);
            } else {
                log.info("Sending notification  " + LogMessage.FAILED + " User does not exist");

            }

        };

        executor.submit(task);
    }
}
