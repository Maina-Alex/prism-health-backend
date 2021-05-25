package com.prismhealth.services;

import com.prismhealth.Models.Users;
import com.prismhealth.Models.UserRoles;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.UserRolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminProviderService {

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private AccountRepository usersRepo;
    @Autowired
    private UserRolesRepo userRolesRepo;

    public Users getProviderById(String id) {
        Optional<Users> user = Optional.ofNullable(usersRepo.findOneByPhone(id));
        if (user.isPresent()) {
            Users u = user.get();
            u.setRoles(userRolesRepo.findAllByUserId(id).stream().map(UserRoles::getRole).collect(Collectors.toList()));

            return u;
        }

        else
            return null;
    }

    public List<Users> getAllProviders() {

        return usersRepo.findAll().stream().filter(u -> Optional.ofNullable(u.getAccountType()).isPresent())
                .filter(u -> u.getAccountType().equals("PROVIDER")).map(u -> {
                    u.setRoles(userRolesRepo.findAllByUserId(u.getPhone()).stream().map(UserRoles::getRole)
                            .collect(Collectors.toList()));
                    return u;
                }).collect(Collectors.toList());

    }

    public boolean deleteProvider(String id) {
        Optional<Users> user = usersRepo.findById(id);
        if (user.isPresent() && !user.get().getEmail().equals("admin@prismhealth.com")) {
            usersRepo.delete(user.get());
            return true;
        } else
            return false;
    }

    public String addUser(Users users) {
        Optional<String> phone = Optional.ofNullable(users.getPhone());

        if (phone.isPresent()) {
            Optional<Users> uOptional = usersRepo.findById(phone.get());
            if (uOptional.isPresent()) {

                return "User already exits";

            } else {
                insertUser(users);
                return "User added successfully";

            }

        } else
            return "Please provide an phone number";

    }

    private void updateUser(Users users, Principal principal) {

        users.setAccountType("PROVIDER");
        users.setBlocked(false);
        users.setDeleted(false);
        users.setVerified(true);
        Optional<Users> u = Optional.ofNullable(usersRepo.findOneByPhone(users.getPhone()));
        users.setPassword(u.get().getPassword());
        if (u.get().getEmail().equals("admin@healthprism.com"))
            users.setEmail(u.get().getEmail());

        users = usersRepo.save(users);

        if (!u.get().getEmail().equals("admin@healthprism.com")) {
            userRolesRepo.deleteAll(userRolesRepo.findAllByUserId(users.getPhone()));

            UserRoles role = new UserRoles();
            role.setAssignedBy(principal.getName());
            role.setRole("ROLE_PROVIDER");

            userRolesRepo.save(role);

        }

    }

    private void insertUser(Users users) {
        users.setAccountType("PROVIDER");
        users.setBlocked(false);
        users.setDeleted(false);
        users.setVerified(true);

        users.setPassword(encoder.encode(users.getPassword()));
        users = usersRepo.save(users);
        userRolesRepo.deleteAll(userRolesRepo.findAllByUserId(users.getPhone()));
        UserRoles role = new UserRoles();
        role.setAssignedBy("self");
        role.setRole("ROLE_PROVIDER");
        userRolesRepo.save(role);

    }

}
