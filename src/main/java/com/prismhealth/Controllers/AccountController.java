package com.prismhealth.Controllers;

import com.prismhealth.Models.PasswordReset;
import com.prismhealth.Models.UserRating;
import com.prismhealth.Models.Users;
import com.prismhealth.dto.Request.Phone;

import com.prismhealth.dto.Request.SignUpRequest;

import com.prismhealth.dto.Request.UpdateForgotPasswordReq;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.services.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;
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

    @GetMapping
    public ResponseEntity<?> getUser(Principal principal){
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
    public ResponseEntity<SignUpResponse> updateUser(@RequestBody Users users) {
        return accountService.updateUser(users);
    }

    @ApiOperation(value = "Authenticate phone by sending otp")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User already exists") })
    @PostMapping("/authentication")
    public ResponseEntity<SignUpResponse> authentication(@RequestBody Phone phone) {
        return accountService.authentication(phone);
    }

    @ApiOperation(value = "Make request to change password")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody Phone phone) {

        return accountService.forgotPassword(phone);
    }
    @ApiOperation(value = "Make request to update password after receiving code")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/updatePassword")

    public ResponseEntity<?> updatePassword(@RequestBody UpdateForgotPasswordReq req){
        return accountService.updateForgotPassword(req);
    }

    @GetMapping("/getProviderById")
    public ResponseEntity<?> getProviderById(@RequestParam String providerId) {
        return accountService.getProviderById(providerId);
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

    @ApiOperation(value = "actually change password")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordReset password) {
        return accountService.changePassword(password);

    }

    @PostMapping("/postReviews")
    public List<UserRating> postReview(@RequestBody UserRating userRating) {
        return accountService.addUserReview(userRating);
    }

    @PostMapping("/postRating")
    public Map<String, Integer> postRating(@RequestBody UserRating userRating) {
        return accountService.addUserRatings(userRating);
    }

    /*
     * GETS
     */
    @GetMapping("/getReviews")
    public List<UserRating> getReviews(@RequestParam String id) {
        return accountService.getUserReview(id);
    }
}
