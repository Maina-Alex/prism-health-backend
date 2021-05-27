package com.prismhealth.Controllers;

import java.security.Principal;
import java.util.List;

import com.prismhealth.Models.Users;
import com.prismhealth.services.AdminStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/staff")
@CrossOrigin
public class AdminStaffControler {
    @Autowired
    private AdminStaffService staffService;

    @GetMapping("/id/{phone}")
    public Users getStaffById(@PathVariable("phone") String phone) {
        return staffService.getStaffById(phone);

    }

    @PostMapping
    public String addUser(@RequestBody Users users, Principal principal) {
        return staffService.addUser(users, principal);
    }

    @GetMapping
    public List<Users> getAllStaff() {
        return staffService.getAllStaff();

    }

    @CrossOrigin
    @PostMapping("/delete")
    public boolean deleteStaff(@RequestBody Users users) {
        return staffService.deleteStaff(users.getPhone());
    }

}
