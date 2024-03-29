package com.prismhealth.services;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.prismhealth.Models.Users;
import com.prismhealth.Models.UserRoles;
import com.prismhealth.repository.UserRepository;
import com.prismhealth.repository.UserRolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminStaffService {

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private UserRepository usersRepo;
    @Autowired
    private UserRolesRepo userRolesRepo;

    public Users getStaffById(String id) {
        Optional<Users> user = Optional.ofNullable(usersRepo.findByPhone(id));
        if (user.isPresent()) {
            Users u = user.get();
            u.setRoles(userRolesRepo.findAllByUserId(id).stream().map(UserRoles::getRole).collect(Collectors.toList()));

            return u;
        }

        else
            return null;
    }

    public List<Users> getAllStaff() {

        return usersRepo.findAll().stream().filter(u -> Optional.ofNullable(u.getAccountType()).isPresent())
                .filter(u -> u.getAccountType().equals("STAFF")).map(u -> {
                    u.setRoles(userRolesRepo.findAllByUserId(u.getPhone()).stream().map(UserRoles::getRole)
                            .collect(Collectors.toList()));
                    return u;
                }).collect(Collectors.toList());

    }

    public boolean deleteStaff(String id) {
        Optional<Users> user = usersRepo.findById(id);
        if (user.isPresent() && !user.get().getEmail().equals("admin@prismhealth.com")) {
            usersRepo.delete(user.get());
            return true;
        } else
            return false;
    }

    public String addUser(Users users, Principal principal) {
        Optional<String> email = Optional.ofNullable(users.getEmail());

        if (email.isPresent()) {
            Optional<Users> uOptional = usersRepo.findOneByEmail(email.get());
            if (uOptional.isPresent()) {
                // handleUpdates
                users.setPhone(uOptional.get().getPhone());
                updateUser(users, principal);
                return "User details updated successfully";

            } else {
                insertUser(users, principal);
                return "User added successfully";

            }

        } else
            return "Please provide an email address";

    }

    private void updateUser(Users users, Principal principal) {

        users.setAccountType("STAFF");
        users.setBlocked(false);
        users.setDeleted(false);
        users.setVerified(true);
        Optional<Users> u = Optional.ofNullable(usersRepo.findByPhone(users.getPhone()));
        users.setPassword(u.get().getPassword());
        if (u.get().getEmail().equals("admin@healthprism.com"))
            users.setEmail(u.get().getEmail());

        users = usersRepo.save(users);

        if (!u.get().getEmail().equals("admin@healthprism.com")) {
            userRolesRepo.deleteAll(userRolesRepo.findAllByUserId(users.getPhone()));
            for (String s : users.getRoles()) {
                UserRoles role = new UserRoles();
                role.setAssignedBy(principal.getName());
                role.setRole(s);

                userRolesRepo.save(role);

            }

        }

    }

    private void insertUser(Users users, Principal principal) {
        users.setAccountType("STAFF");
        users.setBlocked(false);
        users.setDeleted(false);
        users.setVerified(true);

        users.setPassword(encoder.encode(users.getPassword()));
        users = usersRepo.save(users);
        userRolesRepo.deleteAll(userRolesRepo.findAllByUserId(users.getPhone()));

        for (String s : users.getRoles()) {
            UserRoles role = new UserRoles();
            role.setAssignedBy(principal.getName());
            role.setRole(s);
            userRolesRepo.save(role);

        }
    }

}