package com.prismhealth.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.prismhealth.Models.UserRoles;
import com.prismhealth.repository.UserRolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private UserRolesRepo roleRepo;

    public UserRoles addRoles(UserRoles role, Principal principal) {
        role.setDeleted(false);
        role.setApproved(true);
        role.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        role.setCreated_by("+254719285868");
        return roleRepo.save(role);

    }
    // Get role names

    public List<String> getRolesNames() {
        return roleRepo.findAll(Sort.by("timestamp").descending()).stream()
                .filter(role -> role.isApproved() && !role.isDeleted()).map(UserRoles::getRole)
                .collect(Collectors.toList());

    }

    // approve Role
    public UserRoles approveRoleById(String id, Principal principal) {
        Optional<UserRoles> role = roleRepo.findById(id);
        if (role.isPresent()) {
            UserRoles appRole = role.get();
            appRole.setApproved(true);
            appRole.setApprovedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            appRole.setApprovedBy(principal.getName());

            return roleRepo.save(appRole);

        } else
            return null;

    }

    public List<UserRoles> getAllRoles() {
        return roleRepo.findAll(Sort.by("timestamp").descending()).stream()
                .filter(role -> role.isApproved() && !role.isDeleted()).collect(Collectors.toList());
    }

    // delete role by Id

    public UserRoles deleteById(String id, Principal principal) {
        Optional<UserRoles> role = roleRepo.findById(id);
        if (role.isPresent()) {
            UserRoles delRole = role.get();
            delRole.setApprove_delete(false);
            delRole.setDeletedBy(principal.getName());
            return roleRepo.save(delRole);

        } else
            return null;

    }

    // approve role deletion
    public UserRoles approveDeleteById(String id, Principal principal) {
        Optional<UserRoles> role = roleRepo.findById(id);
        if (role.isPresent()) {
            UserRoles delRole = role.get();
            delRole.setApprove_delete(true);
            delRole.setApprove_deleteBy(principal.getName());
            delRole.setDeleted(true);
            delRole.setDeletedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            return roleRepo.save(delRole);

        } else
            return null;

    }

}
