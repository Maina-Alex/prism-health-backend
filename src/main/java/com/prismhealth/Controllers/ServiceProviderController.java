package com.prismhealth.Controllers;

import java.awt.*;
import java.security.Principal;
import java.util.List;

import com.prismhealth.Models.Bookings;
import com.prismhealth.Models.Services;
import com.prismhealth.services.ServiceBookingService;
import com.prismhealth.services.ServiceProviderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "ServiceProvider Apis")
@RestController
@CrossOrigin
@RequestMapping("/services")
public class ServiceProviderController {
    @Autowired
    private ServiceProviderService serviceProviderService;

    @GetMapping("/providers/bookings")
    public List<Bookings> getAllBookings(Principal principal) {
        return serviceProviderService.getAllServicesBookings(principal);

    }
    @PostMapping("/users/service/availability/false")
    public Services setServiceAvailabilityFalse(@RequestBody List<Bookings> bookings) {
        return serviceProviderService.setServiceAvailabilityFalse(bookings);
    }

    @PostMapping("/users/service/availability/true")
    public Services setServiceAvailabilityTrue(@RequestBody List<Bookings> bookingsIds) {
        return serviceProviderService.setServiceAvailabilityTrue(bookingsIds);
    }

    @PostMapping("/providers/services")
    public Services createService(@RequestParam String services, @RequestParam MultipartFile[] multipartFile){
        return serviceProviderService.createService(services,multipartFile);
    }
    @GetMapping("/users/all")
    public List<Services> getAllService(){
        return serviceProviderService.getAllServices();
    }
    @GetMapping("/users/{providerId}")
    public List<Services> getServiceByProviderId(@PathVariable String providerId){
        return serviceProviderService.getServicesByProvider(providerId);
    }
    @GetMapping("/users/near")
    public List<Services> getAllServiceNear(){
        Distance distance=new Distance(0);
        Point point = new Point(1.0,23.9);
        return serviceProviderService.getServicesNear(point,distance);
    }
    @GetMapping("/users/{serviceName}")
    public List<Services> getAllServiceByName(@PathVariable String serviceName){
        return serviceProviderService.getServicesByName(serviceName);
    }
}
