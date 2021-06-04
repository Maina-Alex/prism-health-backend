package com.prismhealth.services;

import com.prismhealth.Models.Users;
import com.prismhealth.Models.UserRoles;
import com.prismhealth.dto.Request.AddProviderReq;
import com.prismhealth.dto.Request.UpdateProviderRequest;
import com.prismhealth.repository.UserRepository;
import com.prismhealth.repository.UserRolesRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminProviderService {
    private final BCryptPasswordEncoder encoder;
    private final UserRepository usersRepo;
    private final UserRolesRepo userRolesRepo;

    public ResponseEntity<?> getProviderById(String phone) {
        Optional<Users> user = Optional.ofNullable(usersRepo.findByPhone(phone));
        if (user.isPresent()) {
            Users u = user.get();
            u.setRoles(
                    userRolesRepo.findAllByUserId(phone).stream().map(UserRoles::getRole).collect(Collectors.toList()));

            return ResponseEntity.ok(u);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public List<Users> getAllProviders() {

        return usersRepo.findAll().stream().filter(u -> Optional.ofNullable(u.getAccountType()).isPresent())
                .filter(u -> !u.isDeleted()).filter(u -> u.getAccountType().equals("PROVIDER"))
                .peek(u -> u.setRoles(userRolesRepo.findAllByUserId(u.getPhone()).stream().map(UserRoles::getRole)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> blockProvider(String phone) {
        Optional<Users> user = Optional.ofNullable(usersRepo.findByPhone(phone));
        if (user.isPresent() && !user.get().getEmail().equals("admin@prismhealth.com")) {
            Users prov = user.get();
            prov.setBlocked(true);
            usersRepo.save(prov);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> unBlock(String phone) {
        Optional<Users> user = Optional.ofNullable(usersRepo.findByPhone(phone));
        if (user.isPresent() && !user.get().getEmail().equals("admin@prismhealth.com")) {
            Users prov = user.get();
            prov.setBlocked(false);
            usersRepo.save(prov);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> delete(String phone) {
        Optional<Users> user = Optional.ofNullable(usersRepo.findByPhone(phone));
        if (user.isPresent() && !user.get().getEmail().equals("admin@prismhealth.com")) {
            String[] p = phone.split("/+");
            Users prov = user.get();
            prov.setDeleted(true);
            prov.setPhone(p[1]);
            usersRepo.save(prov);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> addProvider(AddProviderReq req) {
        Optional<Users> u = Optional.ofNullable(usersRepo.findByPhone(req.getPhone()));
        if (req.getEmail().equals("admin@healthprism.com")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credentials not allowed");
        }
        if (u.isEmpty()) {
            Users users = new Users();
            users.setFirstName(req.getFirstName());
            users.setSecondName(req.getSecondName());
            users.setPassword(encoder.encode(req.getPassword()));
            users.setEmail(req.getEmail());
            users.setPosition(req.getPosition());
            users.setPhone(req.getPhone());
            users.setAccountType("PROVIDER");
            users.setBlocked(false);
            users.setDeleted(false);
            users.setVerified(true);
            users.setVerifiedOn(new Date());

            UserRoles role = new UserRoles();
            role.setAssignedBy("self");
            role.setRole("ROLE_PROVIDER");
            role.setUserId(users.getPhone());
            userRolesRepo.save(role);

            return ResponseEntity.ok(usersRepo.save(users));
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User already exists");

    }

    public ResponseEntity<?> updateProvider(UpdateProviderRequest req) {
        Optional<Users> provider = Optional.ofNullable(usersRepo.findByPhone(req.getOldPhone()));
        if (provider.isPresent()) {
            Users prov = provider.get();
            if (req.getNewPhone() != null && !req.getNewPhone().equals(""))
                prov.setPhone(req.getNewPhone());
            if (req.getEmail() != null && !req.getEmail().equals(""))
                prov.setEmail(req.getEmail());
            if (req.getFirstName() != null && !req.getFirstName().equals(""))
                prov.setFirstName(req.getFirstName());
            if (req.getSecondName() != null && !req.getSecondName().equals(""))
                prov.setSecondName(req.getSecondName());
            if (req.getPassword() != null && !req.getPassword().equals(""))
                prov.setPassword(encoder.encode(req.getPassword()));
            if (req.getPosition().length > 1)
                prov.setPosition(req.getPosition());
            return ResponseEntity.ok(usersRepo.save(prov));
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Provider not found");

    }
}
