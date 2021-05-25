package com.prismhealth.Controllers;

import com.prismhealth.Models.Users;
import com.prismhealth.services.AdminProviderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "AdminProvider Apis")
@RestController
@RequestMapping("/admin/providers")
@CrossOrigin
public class AdminProviderController {
    @Autowired
    private AdminProviderService providerService;

    @GetMapping("/id/{id}")
    public Users getProviderById(@PathVariable("id") String id) {
        return providerService.getProviderById(id);

    }

    @PostMapping
    public String addUser(@RequestBody Users users) {
        return providerService.addUser(users);
    }

    @GetMapping
    public List<Users> getAllProviders() {
        return providerService.getAllProviders();

    }

    @CrossOrigin
    @PostMapping("/delete")
    public boolean deleteProvider(@RequestBody Users users) {
        return providerService.deleteProvider(users.getPhone());
    }

}
