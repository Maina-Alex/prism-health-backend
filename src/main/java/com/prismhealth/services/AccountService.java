package com.prismhealth.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.User;
import com.prismhealth.dto.Request.SignInRequest;
import com.prismhealth.dto.Request.SignUpRequest;
import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
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
        Optional<User> user = accountRepository.findOneByPhone(signInRequest.getPhone());
        if (user .isPresent()){
        response.setUser(user.get());
        response.setMessage("successful login");
        }else {
            response.setUser(null);
            response.setMessage("User not found !!");
        }

        return ResponseEntity.ok(response);
    }
    public ResponseEntity<SignUpResponse> signUpUser(SignUpRequest signUpRequest){
        SignUpResponse signUpResponse = new SignUpResponse();
        String authCode = authService.getAuthentication(signUpRequest);
        User user1= new User();
        user1.setPassword(signUpRequest.getPassword());
        user1.setPhone(signUpRequest.getPhone());
        user1.setEmail(signUpRequest.getEmail());
        user1.setFirstName(signUpRequest.getFirstName());
        user1.setSecondName(signUpRequest.getSecondName());
        user1.setRole(signUpRequest.getRole());
        user1.setEmergencyContact1(null);
        user1.setEmergencyContact2(null);

            signUpResponse.setMessage("successfully sent");
            signUpResponse.setUser(user1);
            signUpResponse.setAuthCode(authCode);

        return ResponseEntity.ok(signUpResponse);
    }
    public ResponseEntity<User> saveAuthenticUser(SignUpResponse response,String code) throws InstanceAlreadyExistsException {
        //TODO create provider sign up
        if (response.getAuthCode().equals(code)){
        if (accountRepository.findOneByPhone(response.getUser().getPhone()).isPresent()){
            throw new InstanceAlreadyExistsException();
        }
       return ResponseEntity.ok().body(accountRepository.save(response.getUser()));}
        else try {
            throw new InvalidObjectException("Invalid user");
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        } return null;
    }
}
