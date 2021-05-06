package com.prismhealth.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismhealth.Models.User;
import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Request.SignInRequest;
import com.prismhealth.dto.Request.SignUpRequest;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.security.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;
    public AccountService(AccountRepository accountRepository , ObjectMapper objectMapper){
        this.accountRepository = accountRepository;
        this.objectMapper = objectMapper;
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
        User user1= new User();
        user1.setId(signUpRequest.getPhone());
        user1.setPassword(signUpRequest.getPassword());
        user1.setPhone(signUpRequest.getPhone());
        user1.setEmail(signUpRequest.getEmail());
        user1.setFirstName(signUpRequest.getFirstName());
        user1.setSecondName(signUpRequest.getSecondName());
        user1.setRole(signUpRequest.getRole());
        try {
            User user = objectMapper.readValue(accountRepository.save(user1).toString(),User.class);
            if (user != null){
            signUpResponse.setUser(user);
            signUpResponse.setMessage("successful created");
            }else {
                signUpResponse.setUser(null);
                signUpResponse.setMessage("SignUp failed!!");
            }
        } catch (JsonProcessingException e) {
            log.error(String.format("SignUp failed: %s",e.getLocalizedMessage()));
        }
        return ResponseEntity.ok(signUpResponse);
    }
}
