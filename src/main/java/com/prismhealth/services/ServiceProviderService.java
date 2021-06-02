package com.prismhealth.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.prismhealth.Models.*;
import com.prismhealth.config.Constants;
import com.prismhealth.dto.Request.CreateServiceReq;
import com.prismhealth.repository.*;

import com.prismhealth.util.LogMessage;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ServiceProviderService {
    private final UserRepository usersRepo;
    private final ServiceRepo serviceRepo;
    private final BookingsRepo bookingsRepo;
    MailService mailService;
    UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public List<Bookings> getAllServicesBookings(Principal principal) {
        Optional<Users> optional = Optional.ofNullable(usersRepo.findByPhone(principal.getName()));
        if (optional.isPresent()) {
            return bookingsRepo.findAllByUserId(optional.get().getPhone(), Sort.by("timestamp").descending());
        }

        else
            return new ArrayList<>();
    }

    public ResponseEntity<?> createService(CreateServiceReq req, Principal principal) {
        String providerPhone=principal.getName();
        Services service=new Services();
        if(req.getProviderPhone()!=null) {
            providerPhone= req.getProviderPhone();
        }
       Users provider=usersRepo.findByPhone(providerPhone);
       if(provider!=null){
           service.setProviderPhone(provider.getPhone());
           service.setPosition(req.getPosition());
           service.setName(req.getName());
           service.setDescription(req.getDescription());
           service.setCharges(req.getCharges());
           service.setVerified(true);
           service.setImages(req.getImages());
           service.setSubCategory(req.getSubCategory());
           Services saved=serviceRepo.save(service);
           sendEmail(provider.getEmail(), service.getName());
           List<Notice> notices=provider.getNotifications().getNotices();
           Notice notice=new Notice();
           notice.setEmail(provider.getEmail());
           notice.setUserId(provider.getEmail());
           notice.setMessage("You created a service with the name " + service.getName());
           notices.add(notice);
           provider.getNotifications().setNotices(notices);
           usersRepo.save(provider);
           return ResponseEntity.ok(saved);
           }
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No provider registered with that number");
    }

    public List<Services> getAllServices() {
        return serviceRepo.findAll();
    }

    public List<Services> getServiceByName(String serviceName) {
        return serviceRepo.findAll().stream().filter(services -> services.getName().equalsIgnoreCase(serviceName))
                .collect(Collectors.toList());
    }
    public Services getServiceById(String serviceId) {
        Optional<Services> services = serviceRepo.findById(serviceId);
        return services.orElse(null);
    }

    public List<Services> getServicesByProvider(String providerPhone) {
        return serviceRepo.findAllByProviderPhone(providerPhone);
    }

    public List<Services> getServicesNear(Point location, Distance distance) {
        return serviceRepo.findByPositionNear(location, distance);
    }

    @Async
    public void sendEmail(String email, String serviceName) {
        Mail mail=new Mail();
        mail.setMailTo(email);
        mail.setMailSubject("Service Created");
        mail.setMailContent("Great, Your are now a service provider for the following service : "+ serviceName);
        mailService.sendEmail(mail);
        log.info("Sent service creation email to "+ email);
    }

    public ResponseEntity<?> getProvidersByServiceId(String serviceId) {
        Optional<Services> services = serviceRepo.findById(serviceId);
        if(services.isPresent()){
            return ResponseEntity.ok().body(usersRepo.findByPhone(services.get().getProviderPhone()));
        }
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider not found");
    }
}
