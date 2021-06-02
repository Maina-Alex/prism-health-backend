package com.prismhealth.Controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.prismhealth.Models.Bookings;
import com.prismhealth.Models.Services;
import com.prismhealth.dto.Request.CancelBooking;
import com.prismhealth.dto.Request.CreateServiceReq;
import com.prismhealth.services.BookingService;
import com.prismhealth.services.ServiceProviderService;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ServiceProviderController {
    private final ServiceProviderService serviceProviderService;
    private final BookingService bookingService;

    @GetMapping("/providers/bookings")
    public List<Bookings> getAllBookings(Principal principal) {
        return serviceProviderService.getAllServicesBookings(principal);
    }


    @PostMapping("/providers/services")
    public ResponseEntity<?> createService(@RequestBody CreateServiceReq req, Principal principal) {
        return serviceProviderService.createService(req, principal);
    }

    @GetMapping("/getServiceProviders/{serviceId}")
    public ResponseEntity<?> getServiceProviders(@PathVariable String serviceId) {
        return serviceProviderService.getProvidersByServiceId(serviceId);
    }

    @GetMapping("/getServiceById/{serviceId}")
    public Services getServiceById(@PathVariable String serviceId) {
        return serviceProviderService.getServiceById(serviceId);
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

    @GetMapping("/users/serviceName/{serviceName}")
    public List<Services> getAllServiceByName(@PathVariable String serviceName) {
        return serviceProviderService.getServiceByName(serviceName);
    }

    @PostMapping("/booking/add")
    public ResponseEntity<?> addServiceBooking(@RequestBody List<Bookings> bookings, Principal principal) {

        return ResponseEntity.ok().body(bookingService.createBookings(bookings, principal));

    }

    @PostMapping("/booking/cancel")
    public ResponseEntity<Map<String, List<Bookings>>> cancelServiceBooking(@RequestBody CancelBooking bookings, Principal principal) {

        return ResponseEntity.ok().body(bookingService.cancelBookings(bookings.getBookingId(), principal));

    }

    @GetMapping("/booking/all")
    public ResponseEntity<?> getBookingHistory(Principal principal) {

        return ResponseEntity.ok().body(bookingService.getBookingsHistory(principal));

    }
}
