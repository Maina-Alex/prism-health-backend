package com.prismhealth.services;

import java.util.List;

import java.util.stream.Collectors;
import com.prismhealth.Models.Users;
import com.prismhealth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AdminService
 */
@Service
public class AdminService {
    private final Logger log = LoggerFactory.getLogger(AdminService.class);
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository usersRepo;

    public List<Users> getAllUsers() {
        log.info("Admin: GET all Verified Users.");

        return usersRepo.findAll().stream().filter(u -> authService.checkUserValidity(u) && u.isVerified())
                .collect(Collectors.toList());

    }

}