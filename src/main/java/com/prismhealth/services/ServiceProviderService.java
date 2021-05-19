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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.Bookings;
import com.prismhealth.Models.Services;
import com.prismhealth.Models.Users;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.BookingsRepo;
import com.prismhealth.repository.ServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServiceProviderService {

    @Autowired
    private AccountRepository usersRepo;
    @Autowired
    private BookingsRepo bookingsRepo;

    @Autowired
    private ServiceBookingService bookingsService;
    @Autowired
    private ServiceRepo serviceRepo;

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
        Optional<Users> optional = usersRepo.findOneByEmail(principal.getName());
        if (optional.isPresent()) {
            return bookingsRepo.findAllByProviderId(optional.get().getPhone(), Sort.by("timestamp").descending());
        }

        else
            return new ArrayList<>();

    }

    public Services createService(String services, MultipartFile multipartFile,Principal principal) {
        /*if(multipartFile==null||multipartFile.length<1){
            throw new MultipartException("is empty");
        }*/
        try {
            Users users = usersRepo.findOneByPhone(principal.getName());
            Services services1 = new ObjectMapper().readValue(services,Services.class);
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            services1.setImages(fileName);
            services1.setProviderId(users.getPhone());
            services1.setLocationName(users.getLocationName());
            services1.setPosition(users.getPosition());
            String uploadDir = "user-photos/" + services1.getName();
            saveFile(uploadDir, fileName, multipartFile);
            return serviceRepo.save(services1);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void saveFile(String uploadDir, String fileName,
                                MultipartFile multipartFile)  {
        try{
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            InputStream inputStream = multipartFile.getInputStream();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch ( IOException ioe) {
            try {
                throw new IOException("Could not save image file: " + fileName, ioe);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Services> getAllServices() {
        return serviceRepo.findAll();
    }
    public List<Services> getServicesByName(String serviceName) {
        return serviceRepo.findAll().stream().filter(services -> services.getName()==serviceName).collect(Collectors.toList());
    }
    public List<Services> getServicesByProvider(String providerId) {
        return serviceRepo.findAllByProviderId(providerId);
    }
    public List<Services> getServicesNear(Point location, Distance distance) {
        return serviceRepo.findByPositionNear(location,distance);
    }
}
