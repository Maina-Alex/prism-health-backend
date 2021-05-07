package com.prismhealth.Controllers;

import com.prismhealth.Models.User;
import com.prismhealth.dto.Request.SignInRequest;
import com.prismhealth.dto.Request.SignUpRequest;
import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;

@RestController
@RequestMapping("api")
public class AccountController {

    private final AccountService accountService;
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity<SignInResponse> login(@RequestBody SignInRequest signInRequest){
        return accountService.loginUser(signInRequest);
    }
    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest signUpRequest){
        return accountService.signUpUser(signUpRequest);
    }
    @PostMapping("/confirm")
    public ResponseEntity<User> userDetailsConfirmation(@RequestBody String receivedAuthCode,@RequestBody SignUpResponse user){
        try {
            return accountService.saveAuthenticUser(user,receivedAuthCode);
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        }
        return null;
    }
    //TODO review the security implementation
    //TODO make endpoint to receiving phone,another to post the remaining user details
    //TODO Create an implementation for change password
    //TODO create an implementation for admin to add provider
    //TODO create an end point for admin login

}
