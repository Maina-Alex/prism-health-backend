package com.prismhealth.Controllers;

import com.prismhealth.Models.User;
import com.prismhealth.dto.Request.SignInRequest;
import com.prismhealth.dto.Request.SignUpRequest;
import com.prismhealth.dto.Request.phone;
import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.services.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
@Api(tags = "Account Apis")
@RestController
@RequestMapping("api")
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @ApiOperation(value = "login user")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found or already exists") })
    @PostMapping("/login")
    public ResponseEntity<SignInResponse> login(@RequestBody SignInRequest signInRequest){
        return accountService.loginUser(signInRequest);
    }
    @ApiOperation(value = "sign up user")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest signUpRequest){
        return accountService.signUpUser(signUpRequest);
    }
    @ApiOperation(value = "Authenticate phone by sending otp")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User already exists") })
    @PostMapping("/authentication")
    public ResponseEntity<SignUpResponse> authentication(@RequestBody phone phone){
        return accountService.authentication(phone);
    }
    @ApiOperation(value = "Make request to change password")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/forgotPassword")
    public ResponseEntity<HttpStatus> forgotPassword(@RequestBody String email){
        return accountService.forgotPassword(email);
    }
    @ApiOperation(value = "actually change password")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PutMapping("/changePassword/{phone}")
    public ResponseEntity<?> changePassword(@PathVariable("phone") String phone, @RequestBody String password){
        return accountService.changePassword(phone,password);
    }

    //TODO review the security implementation
    //TODO create an end point for admin login

}
