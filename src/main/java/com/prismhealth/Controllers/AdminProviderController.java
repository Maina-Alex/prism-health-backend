package com.prismhealth.Controllers;

import com.prismhealth.Models.Users;
import com.prismhealth.dto.Request.AddProviderReq;
import com.prismhealth.dto.Request.UpdateProviderRequest;
import com.prismhealth.services.AdminProviderService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "AdminProvider Apis")
@RestController
@RequestMapping("/admin")
@CrossOrigin
@AllArgsConstructor
public class AdminProviderController {
    private final AdminProviderService providerService;

    @GetMapping("/providers/{phone}")
    public ResponseEntity<?> getProviderById(@PathVariable String phone) {
        return providerService.getProviderById(phone);
    }

    @PostMapping("/providers")
    public ResponseEntity<?> addUser(@RequestBody AddProviderReq req) {
        return providerService.addProvider(req);
    }

    @GetMapping("/providers")
    public List<Users> getAllProviders() {
        return providerService.getAllProviders();

    }

    @PostMapping("/providers/delete")
    public ResponseEntity<?> deleteProvider(@RequestBody Users users) {
        return providerService.deleteProvider(users.getPhone());
    }

    @PostMapping("/providers/update")
    public ResponseEntity<?> updateProvider(@RequestBody UpdateProviderRequest request) {
        return providerService.updateProvider(request);
    }

}
