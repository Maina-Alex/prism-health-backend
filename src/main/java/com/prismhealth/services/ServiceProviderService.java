package com.prismhealth.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.*;
import com.prismhealth.repository.*;
import com.prismhealth.security.SecurityConstants;
import com.prismhealth.util.Actions;
import com.prismhealth.util.AppConstants;
import com.prismhealth.util.HelperUtility;
import com.prismhealth.util.LogMessage;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
public class ServiceProviderService {

    @Autowired
    private AccountRepository usersRepo;
    @Autowired
    private BookingsRepo bookingsRepo;
    @Autowired
    private PhotoRepository photoRepository;
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
        sendEmail(usersRepo.findOneByPhone(service.getProviderId()), "notifyProvider");
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
        Optional<Users> optional = usersRepo.findOneByEmail(principal.getName());
        if (optional.isPresent()) {
            return bookingsRepo.findAllByServiceId(optional.get().getPhone(), Sort.by("timestamp").descending());
        }

        else
            return new ArrayList<>();

    }

    public Services createService(String services, MultipartFile multipartFile, Principal principal) {
        /*
         * if(multipartFile==null||multipartFile.length<1){ throw new
         * MultipartException("is empty"); }
         */
        try {
            Users users = usersRepo.findOneByPhone(principal.getName());
            Services services1 = new ObjectMapper().readValue(services, Services.class);
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            services1.setImages(fileName);
            services1.setProviderId(users.getPhone());
            services1.setLocationName(users.getLocationName());
            services1.setPositions(users.getPositions());
            services1.setPosition(users.getPosition());
            Photos photos = new Photos();
            photos.setPhoto(new Binary(BsonBinarySubType.BINARY, multipartFile.getBytes()));
            sendEmail(users, "createService");
            services1.setImages(photoRepository.save(photos).getId());
            return serviceRepo.save(services1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Services> getAllServices() {
        List<Services> services = serviceRepo.findAll();
        for (Services services1: services){
            services1.setUsers(accountRepository.findOneByPhone(services1.getProviderId()));
        }
        return services;
    }

    public List<Services> getServicesByName(String serviceName) {
        return serviceRepo.findAll().stream().filter(services -> services.getName() == serviceName)
                .collect(Collectors.toList());
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

    public List<Users> getProvidersByServiceId(String serviceId) {
        Optional<Services> services = serviceRepo.findById(serviceId);
        List<Services> servicesList = serviceRepo.findAll().stream()
                .filter(services1 -> services.get().getName().equals(services.get().getName()))
                .collect(Collectors.toList());
        List<Users> usersList = new ArrayList<>();
        for (Services services1 : servicesList) {
            usersList.add(accountRepository.findOneByPhone(services1.getProviderId()));
        }
        return usersList;
    }
}
