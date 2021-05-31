package com.prismhealth.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.prismhealth.Models.*;
import com.prismhealth.repository.*;

import com.prismhealth.util.Actions;

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
    private AccountRepository usersRepo;
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
    AccountRepository accountRepository;
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
        Optional<Users> optional = Optional.ofNullable(usersRepo.findOneByPhone(principal.getName()));
        if (optional.isPresent()) {
            return bookingsRepo.findAllByUserId(optional.get().getPhone(), Sort.by("timestamp").descending());
        }

        else
            return new ArrayList<>();

    }

    public Services createService(Services services, Principal principal) {
        /*
         * if(multipartFile==null||multipartFile.length<1){ throw new
         * MultipartException("is empty"); }
         */
        Positions positions = new Positions();
        if(services.getProviderId()!=null&&services.getLocationName()!=null&&services.getPosition()!=null){
            positions.setLocationName(services.getLocationName());
            if (services.getPosition().length>=2) {
                positions.setLatitude(services.getPosition()[0]);
                positions.setLongitude(services.getPosition()[1]);
                services.setPositions(positions);
            }
            sendEmail(accountRepository.findOneByPhone(services.getProviderId()),"createService");
            return serviceRepo.save(services);
        }else {
        Users users = usersRepo.findOneByPhone(principal.getName());

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
            services1.setProvider(accountRepository.findOneByPhone(services1.getProviderId()));
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
        services.get().setProvider(accountRepository.findOneByPhone(services.get().getProviderId()));
        return services.orElse(null);
    }

    public List<Services> getServicesByProvider(String providerId) {
        return serviceRepo.findAllByProviderId(providerId);
    }

    public List<Services> getServicesNear(Point location, Distance distance) {
        return serviceRepo.findByPositionNear(location, distance);
    }

    public String sendEmail(Users users, String action) {

        if (users == null) {
            return "User with phone number not found";
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

        if (users != null) {
            log.info(message);
            Mail mail = new Mail();
            mail.setMailFrom("prismhealth658@gmail.com");
            mail.setMailTo(users.getEmail());
            mail.setMailSubject("Prism-health Notification services");
            mail.setMailContent(message);

            mailService.sendEmail(mail);
            Notification notification = new Notification();
            notification.setEmail(users.getEmail());
            notification.setUserId(users.getPhone());
            notification.setMessage(message);
            notification.setAction(Actions.RESET_PASSSWORD);
            notification.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            notificationRepo.save(notification);
            log.info("Sent notification to : " + users.getEmail() + " " + LogMessage.SUCCESS);
            return "Notification sent to : " + users.getEmail();

        } else {
            log.info("Sending notification  " + LogMessage.FAILED + " User does not exist");
            return null;
        }

    }

    public Users getProvidersByServiceId(String serviceId) {
        Optional<Services> services = serviceRepo.findById(serviceId);
        if (services.isPresent())
            return accountRepository.findOneByPhone(services.get().getProviderId());
            return null;
    }
}
