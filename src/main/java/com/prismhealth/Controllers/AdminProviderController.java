package com.prismhealth.Controllers;

import com.prismhealth.Models.Users;
import com.prismhealth.services.AdminProviderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "AdminProvider Apis")
@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminProviderController {
    @Autowired
    private AdminProviderService providerService;

    @GetMapping("/providers/id/{id}")
    public Users getProviderById(@PathVariable("id") String id) {
        return providerService.getProviderById(id);

    }

    @PostMapping("/providers")
    public String addUser(@RequestBody Users users) {
        return providerService.addUser(users);
    }

    @GetMapping("/providers")
    public List<Users> getAllProviders() {
        return providerService.getAllProviders();

    }

    @CrossOrigin
    @PostMapping("/providers/delete")
    public boolean deleteProvider(@RequestBody Users users) {
        return providerService.deleteProvider(users.getPhone());
    }

}
