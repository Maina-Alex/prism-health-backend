package com.prismhealth.Controllers;

import java.security.Principal;

import com.prismhealth.Models.UserRoles;
import com.prismhealth.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RoleControler
 */
@RestController
@RequestMapping("/role")
@CrossOrigin
public class RoleControler {
    @Autowired
    private RoleService roleService;

    @PostMapping

    public ResponseEntity<?> addRole(@RequestBody UserRoles role, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.addRoles(role, principal));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok().body(roleService.getAllRoles());
    }

    @GetMapping("/names/all")
    public ResponseEntity<?> getAllRoleNames() {
        return ResponseEntity.ok().body(roleService.getRolesNames());
    }

    @PostMapping("/approverole/{phone}")
    public ResponseEntity<?> approveRoleById(@PathVariable String phone, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.approveRoleById(phone, principal));

    }

    @DeleteMapping("/deleterole/{phone}")
    public ResponseEntity<?> deleteRoleById(@PathVariable String phone, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.deleteById(phone, principal));

    }

    @DeleteMapping("/approvedelete/{phone}")
    public ResponseEntity<?> approveDeleteById(@PathVariable String phone, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.approveDeleteById(phone, principal));

    }

}