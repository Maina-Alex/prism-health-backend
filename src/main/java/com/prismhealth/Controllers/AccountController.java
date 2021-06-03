package com.prismhealth.Controllers;

import com.prismhealth.Models.PasswordReset;
import com.prismhealth.Models.UserReview;
import com.prismhealth.Models.Users;
import com.prismhealth.dto.Request.Phone;

import com.prismhealth.dto.Request.SignUpRequest;

import com.prismhealth.dto.Request.UpdateForgotPasswordReq;
import com.prismhealth.dto.Request.UserUpdateRequest;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.UserRepository;
import com.prismhealth.services.AccountService;
import com.prismhealth.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import java.util.List;

import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@Api(tags = "Account Apis")
@RestController
@RequestMapping("accounts")
@CrossOrigin
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;
    public final UserRepository userRepository;

    @ApiOperation(value = "Get User Object by passing the token")
    @GetMapping
    public ResponseEntity<?> getUser(Principal principal) {
        return accountService.getUsers(principal);
    }

    @ApiOperation(value = "sign up user")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest signUpRequest) {
        return accountService.signUpUser(signUpRequest);
    }

    @ApiOperation(value = "update user")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest request, Principal principal) {
        return accountService.updateUser(request, principal);
    }

    @ApiOperation(value = "Authenticate phone by sending otp")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User already exists") })
    @PostMapping("/authentication")
    public ResponseEntity<SignUpResponse> authentication(@RequestBody Phone phone) {
        return accountService.authentication(phone);
    }

    @ApiOperation(value = "Initiated when user enters phone number and")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody Phone phone) {
        return accountService.forgotPassword(phone);
    }

    @ApiOperation(value = "Make request to update password after receiving code via sms")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UpdateForgotPasswordReq req) {
        return accountService.updateForgotPassword(req);
    }

    @ApiOperation(value = "Gets provider by Id")
    @GetMapping("/getProviderById")
    public ResponseEntity<?> getProviderByPhone(@RequestBody String phone) {
        return accountService.getProviderByPhone(phone);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<Users>> getAllUser() {
        return accountService.getAllUsers();
    }

    @GetMapping("/token")
    public ResponseEntity<?> getUserToken(@RequestHeader("Authorization") String auth) {
        Optional<String> token = Optional.ofNullable(accountService.getToken(auth));

        if (token.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", token.get());
            return new ResponseEntity<>(null, headers, HttpStatus.OK);

        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid User");
    }

    @ApiOperation(value = " change password")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordReset password) {
        return accountService.changePassword(password);

    }

    @PostMapping("/postRating")
    public ResponseEntity<?> postRating(@RequestBody UserReview userRating) {
        return userService.addUserReview(userRating);
    }

    /*
     * GETS
     */
    @GetMapping("/getReviews")
    public List<UserReview> getReviews(@RequestParam String id) {

        return userService.getUserRating(id);
    }
}
