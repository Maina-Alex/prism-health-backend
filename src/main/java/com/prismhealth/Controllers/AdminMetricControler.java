package com.prismhealth.Controllers;

import java.util.HashMap;
import java.util.Map;

import com.prismhealth.repository.BookingsRepo;
import com.prismhealth.repository.ProductsRepository;
import com.prismhealth.repository.ServiceRepo;
import com.prismhealth.services.AdminProviderService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/admin/metric")
@AllArgsConstructor
public class AdminMetricControler {

    private final BookingsRepo bookings;
    private  final ServiceRepo serviceRepo;
    private final ProductsRepository productsRepository;
    private final AdminProviderService provider;

    @GetMapping
    public Map<String, Long> getMetrics() {
        Map<String, Long> map = new HashMap<>();

        map.put("bookings", bookings.count());
        map.put("services", serviceRepo.count());
        map.put("products", productsRepository.count());
        map.put("providers", Long.valueOf("" + provider.getAllProviders().size()));
        return map;

    }

}
