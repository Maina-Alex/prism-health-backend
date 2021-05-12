package com.prismhealth.services;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.prismhealth.Models.User;
import com.prismhealth.Models.UserRoles;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.UserRolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminStaffService {

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private AccountRepository usersRepo;
    @Autowired
    private UserRolesRepo userRolesRepo;

    public User getStaffById(String id) {
        Optional<User> user = Optional.ofNullable(usersRepo.findOneByPhone(id));
        if (user.isPresent()) {
            User u = user.get();
            u.setRoles(userRolesRepo.findAllByUserId(id).stream().map(UserRoles::getRole).collect(Collectors.toList()));

            return u;
        }

        else
            return null;
    }

    public List<User> getAllStaff() {

        return usersRepo.findAll().stream().filter(u -> Optional.ofNullable(u.getAccountType()).isPresent())
                .filter(u -> u.getAccountType().equals("STAFF")).map(u -> {
                    u.setRoles(userRolesRepo.findAllByUserId(u.getPhone()).stream().map(UserRoles::getRole)
                            .collect(Collectors.toList()));
                    return u;
                }).collect(Collectors.toList());

    }

    public boolean deleteStaff(String id) {
        Optional<User> user = usersRepo.findById(id);
        if (user.isPresent() && !user.get().getEmail().equals("admin@prismhealth.com")) {
            usersRepo.delete(user.get());
            return true;
        } else
            return false;
    }

    public String addUser(User user, Principal principal) {
        Optional<String> email = Optional.ofNullable(user.getEmail());

        if (email.isPresent()) {
            Optional<User> uOptional = usersRepo.findOneByEmail(email.get());
            if (uOptional.isPresent()) {
                // handleUpdates
                user.setPhone(uOptional.get().getPhone());
                updateUser(user, principal);
                return "User details updated successfully";

            } else {
                insertUser(user, principal);
                return "User added successfully";

            }

        } else
            return "Please provide an email address";

    }

    private void updateUser(User user, Principal principal) {

        user.setAccountType("STAFF");
        user.setBlocked(false);
        user.setDeleted(false);
        user.setVerified(true);
        Optional<User> u = Optional.ofNullable(usersRepo.findOneByPhone(user.getPhone()));
        user.setPassword(u.get().getPassword());
        if (u.get().getEmail().equals("admin@healthprism.com"))
            user.setEmail(u.get().getEmail());

        user = usersRepo.save(user);

        if (!u.get().getEmail().equals("admin@healthprism.com")) {
            userRolesRepo.deleteAll(userRolesRepo.findAllByUserId(user.getPhone()));
            for (String s : user.getRoles()) {
                UserRoles role = new UserRoles();
                role.setAssignedBy(principal.getName());
                role.setRole(s);

                userRolesRepo.save(role);

            }

        }

    }

    private void insertUser(User user, Principal principal) {
        user.setAccountType("STAFF");
        user.setBlocked(false);
        user.setDeleted(false);
        user.setVerified(true);

        user.setPassword(encoder.encode(user.getPassword()));
        user =usersRepo.save(user);
        userRolesRepo.deleteAll(userRolesRepo.findAllByUserId(user.getPhone()));

        for (String s : user.getRoles()) {
            UserRoles role = new UserRoles();
            role.setAssignedBy(principal.getName());
            role.setRole(s);
            userRolesRepo.save(role);

        }
    }

}
