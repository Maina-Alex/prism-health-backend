package com.prismhealth.Controllers;

import com.prismhealth.Models.EmergencyContactUpdate;
import com.prismhealth.Models.UserRating;
import com.prismhealth.Models.Users;
import com.prismhealth.dto.Request.SignInRequest;
import com.prismhealth.dto.Request.SignUpRequest;
import com.prismhealth.dto.Request.phone;
import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.services.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @ApiOperation(value = "sign up user")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest signUpRequest){
        return accountService.signUpUser(signUpRequest);
    }
    @ApiOperation(value = "update user")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PutMapping("/update")
    public ResponseEntity<SignUpResponse> updateUser(@RequestBody EmergencyContactUpdate ecUpdateRequest){
        return accountService.updateUser(ecUpdateRequest);
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
    public ResponseEntity<?> forgotPassword(@RequestBody String email){

        return accountService.forgotPassword(email);
    }
    @GetMapping("/getProviderById")
    public ResponseEntity<?> getProviderById(@RequestParam String providerId){
        return accountService.getProviderById(providerId);
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
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PutMapping("/changePassword/{phone}")
    public ResponseEntity<?> changePassword(@PathVariable("phone") String phone, @RequestBody String password){
        return accountService.changePassword(phone,password);
    }
    @GetMapping
    public ResponseEntity<?> getUsers(Principal principal) {
        return accountService.getUsers(principal);
    }

    //TODO review the security implementation
    //TODO create an end point for admin login
    /*Ratings and Reviews
    * POSTS
    * */
    @PostMapping("/postReviews")
    public List<UserRating> postReview(@RequestBody UserRating userRating){
        return accountService.addUserReview(userRating);
    }
    @PostMapping("/postRating")
    public Map<String, Integer> postRating(@RequestBody UserRating userRating){
        return accountService.addUserRatings(userRating);
    }
    /*
      GETS
     */
    @GetMapping("/getReviews")
    public List<UserRating> getReviews(@RequestParam String id){
        return accountService.getUserReview(id);
    }
}
