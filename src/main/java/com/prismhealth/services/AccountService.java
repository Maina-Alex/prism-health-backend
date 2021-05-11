package com.prismhealth.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.User;
import com.prismhealth.dto.Request.SignInRequest;
import com.prismhealth.dto.Request.SignUpRequest;
import com.prismhealth.dto.Request.phone;
import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.io.InvalidObjectException;
import java.util.Optional;

@Slf4j
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AuthService authService;
    public AccountService(AccountRepository accountRepository, AuthService authService){
        this.accountRepository = accountRepository;
        this.authService = authService;
    }
    public ResponseEntity<SignInResponse> loginUser(SignInRequest signInRequest){
        SignInResponse response = new SignInResponse();
        User user = accountRepository.findOneByPhone(signInRequest.getPhone());
        if (user!=null&& user.getPassword().equals(signInRequest.getPassword())){
        response.setUser(user);
        response.setMessage("successful login");
        }else {
            response.setUser(null);
            response.setMessage("Please check your username or password is wrong!!");
        }

        return ResponseEntity.ok(response);
    }
    public ResponseEntity<SignUpResponse> authentication(phone phone) {
        SignUpResponse signUpResponse = new SignUpResponse();
        User user = accountRepository.findOneByPhone(phone.getPhone());
        if (user!=null){
            log.info("phone->"+user.getPhone());
             signUpResponse.setMessage("user already exists");
             return ResponseEntity.badRequest().body(signUpResponse);
        }else {
        String authCode = authService.getAuthentication(phone.getPhone());
        signUpResponse.setMessage("Create new user..");
        signUpResponse.setAuthCode(authCode);
        }
        return ResponseEntity.ok(signUpResponse);
    }

    public ResponseEntity<SignUpResponse> signUpUser(SignUpRequest signUpRequest){
        SignUpResponse signUpResponse = new SignUpResponse();
        User user1= new User();
        user1.setPassword(signUpRequest.getPassword());
        user1.setPhone(signUpRequest.getPhone());
        user1.setEmail(signUpRequest.getEmail());
        user1.setFirstName(signUpRequest.getFirstName());
        user1.setSecondName(signUpRequest.getSecondName());
        user1.setRole(signUpRequest.getRole());
        user1.setGender(signUpRequest.getGender());
        user1.setDateOfBirth(signUpRequest.getDateOfBirth());
        user1.setEmergencyContact1(null);
        user1.setEmergencyContact2(null);

            signUpResponse.setMessage("successfully created");
            signUpResponse.setUser(accountRepository.save(user1));

        return ResponseEntity.ok().body(signUpResponse);
    }

    public ResponseEntity<HttpStatus> forgotPassword(String email) {
       //TODO implement the notification service to send the change password link.
        log.info("Send link to email "+email);
        return ResponseEntity.ok().body(HttpStatus.OK);
    }

    public ResponseEntity<?> changePassword(String phone, String password) {
        User user;

            user = accountRepository.findOneByPhone(phone);
            if (user!=null){
            user.setPassword(password);
            return ResponseEntity.ok(accountRepository.save(user));
        }
        return ResponseEntity.badRequest().body("User does not exist");
    }
}
