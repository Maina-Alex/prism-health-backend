package com.prismhealth.Controllers;

import java.security.Principal;
import java.util.List;

import com.prismhealth.Models.Bookings;
import com.prismhealth.Models.Services;
import com.prismhealth.Models.Users;
import com.prismhealth.services.BookingService;
import com.prismhealth.services.ServiceProviderService;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;

import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "ServiceProvider Apis")
@RestController
@CrossOrigin
@RequestMapping("/services")
public class ServiceProviderController {
    @Autowired
    private ServiceProviderService serviceProviderService;
    @Autowired
    BookingService bookingService;

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
    public Services createService(@RequestBody Services services, Principal principal) {
        return serviceProviderService.createService(services, principal);
    }

    @GetMapping("/getServiceProviders")
    public List<Users> getServiceProviders(@RequestParam String serviceId) {
        return serviceProviderService.getProvidersByServiceId(serviceId);
    }

    @GetMapping("/users/all")
    public List<Services> getAllService() {
        return serviceProviderService.getAllServices();
    }

    @GetMapping("/users/{providerId}")
    public List<Services> getServiceByProviderId(@PathVariable String providerId) {
        return serviceProviderService.getServicesByProvider(providerId);
    }

    @GetMapping("/users/near")
    public List<Services> getAllServiceNear(@RequestParam("longitude") double longitude,
            @RequestParam("latitude") double latitude, @RequestParam("radious") double radious) {
        Distance distance = new Distance(radious, Metrics.KILOMETERS);
        Point point = new Point(longitude, latitude);
        return serviceProviderService.getServicesNear(point, distance);
    }

    @GetMapping("/users/{serviceName}")
    public List<Services> getAllServiceByName(@PathVariable String serviceName) {
        return serviceProviderService.getServicesByName(serviceName);
    }

    @PostMapping("/booking/add")
    public ResponseEntity<?> addServiceBooking(List<Bookings> bookings, Principal principal) {

        return ResponseEntity.ok().body(bookingService.createBookings(bookings, principal));

    }
}
