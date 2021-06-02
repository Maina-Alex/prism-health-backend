package com.prismhealth.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.prismhealth.Models.*;
import com.prismhealth.config.Constants;
import com.prismhealth.repository.*;

import com.prismhealth.util.LogMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import org.springframework.stereotype.Service;

@Service
public class ServiceProviderService {

    @Autowired
    private UserRepository usersRepo;
    @Autowired
    private BookingsRepo bookingsRepo;
    @Autowired
    private BookingService bookingsService;
    @Autowired
    private ServiceRepo serviceRepo;
    @Autowired
    NotificationRepo notificationRepo;
    @Autowired
    MailService mailService;
    @Autowired
    UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Services setServiceAvailabilityFalse(List<Bookings> bookings) {
        bookings.stream().map(b -> {
            b.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            return b;
        }).forEach(bookingsRepo::save);
        Services service = serviceRepo.findById(bookings.get(0).getServiceId()).get();
        service.setBookings(bookingsService.getServiceBookings(service.getId()));
        return service;
    }

    public Services setServiceAvailabilityTrue(List<Bookings> bookings) {
        bookings.forEach(b -> {
            b.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            b.setCancelled(true);
            bookingsRepo.save(b);
        });

        Services services = serviceRepo.findById(bookings.get(0).getServiceId()).get();
        services.setBookings(bookingsService.getServiceBookings(services.getId()));
        return services;
    }

    public List<Bookings> getAllServicesBookings(Principal principal) {
        Optional<Users> optional = Optional.ofNullable(usersRepo.findByPhone(principal.getName()));
        return optional.map(users -> users.getBookings().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    public Services createService(Services services, Principal principal) {
        Positions positions = new Positions();
        if(services.getProviderId()!=null&&services.getLocationName()!=null&&services.getPosition()!=null){
            positions.setLocationName(services.getLocationName());
            if (services.getPosition().length>=2) {
                positions.setLatitude(services.getPosition()[0]);
                positions.setLongitude(services.getPosition()[1]);
                services.setPositions(positions);
            }
            sendEmail(userRepository.findByPhone(services.getProviderId()),"createService");
            return serviceRepo.save(services);
        }else {
        Users users = usersRepo.findByPhone(principal.getName());

        services.setProviderId(users.getPhone());
        services.setLocationName(users.getLocationName());
        services.setPositions(users.getPositions());
        services.setPosition(users.getPosition());

        sendEmail(users,"createService");
        return serviceRepo.save(services);
        }
    }

    public List<Services> getAllServices() {
        List<Services> services = serviceRepo.findAll();
        for (Services services1 : services) {
            services1.setProvider(userRepository.findByPhone(services1.getProviderId()));
            services1.setBookings(bookingsService.getServiceBookings(services1.getId()));
        }
        return services;
    }

    public List<Services> getServicesByName(String serviceName) {
        return serviceRepo.findAll().stream().filter(services -> services.getName() == serviceName)
                .collect(Collectors.toList());
    }
    public Services getServicesById(String serviceId) {
        Optional<Services> services = serviceRepo.findById(serviceId);
        services.get().setProvider(userRepository.findByPhone(services.get().getProviderId()));
        return services.orElse(null);
    }

    public List<Services> getServicesByProvider(String providerId) {
        return serviceRepo.findAllByProviderId(providerId);
    }

    public List<Services> getServicesNear(Point location, Distance distance) {
        return serviceRepo.findByPositionNear(location, distance);
    }

    public void sendEmail(Users users, String action) {

        if (users == null) {
            return;
        }
        String message = null;
        if (action.equals("createAccount")) {
            message = "Account successfully created for " + users.getPhone();
        } else if (action.equals("createProduct")) {
            message = "Product successfully created by " + users.getPhone() + " " + users.getEmail();
        } else if (action.equals("createService")) {
            message = "Service successfully created by " + users.getPhone() + " " + users.getEmail();
        } else if (action.equals("createBooking")) {
            message = "Booking successfully created by " + users.getPhone() + " " + users.getEmail();
        } else if (action.equals("notifyProvider")) {
            message = "Product booking made for your product";
        }

        log.info(message);
        Notification notification = new Notification();
        notification.setEmail(users.getEmail());
        notification.setUserId(users.getPhone());
        notification.setMessage(message);
        notification.setAction(null);
        notification.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        notificationRepo.save(notification);
        log.info("Sent notification to : " + users.getEmail() + " " + LogMessage.SUCCESS);
        Mail mail = new Mail();
        mail.setMailFrom(Constants.email);
        mail.setMailTo(users.getEmail());
        mail.setMailSubject("Prism-health Notification services");
        mail.setMailContent(message);

        mailService.sendEmail(mail);

    }

    public Users getProvidersByServiceId(String serviceId) {
        Optional<Services> services = serviceRepo.findById(serviceId);
        return services.map(value -> userRepository.findByPhone(value.getProviderId())).orElse(null);
    }
}
