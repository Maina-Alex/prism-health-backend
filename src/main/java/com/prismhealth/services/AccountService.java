package com.prismhealth.services;

import com.prismhealth.Models.User;
import com.prismhealth.Models.UserRating;
import com.prismhealth.Models.UserRoles;
import com.prismhealth.dto.Request.SignInRequest;
import com.prismhealth.dto.Request.SignUpRequest;
import com.prismhealth.dto.Request.phone;
import com.prismhealth.dto.Response.SignInResponse;
import com.prismhealth.dto.Response.SignUpResponse;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.UserRatingsRepo;
import com.prismhealth.repository.UserRolesRepo;
import com.prismhealth.util.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AuthService authService;
    private final UserRatingsRepo userRatingsRepo;
    private final UserRolesRepo userRolesRepo;

    public AccountService(AccountRepository accountRepository, AuthService authService, UserRatingsRepo userRatingsRepo, UserRolesRepo userRolesRepo){
        this.accountRepository = accountRepository;
        this.authService = authService;
        this.userRatingsRepo = userRatingsRepo;
        this.userRolesRepo = userRolesRepo;
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

        User thisUser = accountRepository.findOneByPhone(signUpRequest.getPhone());
            if (thisUser==null) {
                User user1= new User();
                user1.setPassword(signUpRequest.getPassword());
                user1.setPhone(signUpRequest.getPhone());
                user1.setEmail(signUpRequest.getEmail());
                user1.setFirstName(signUpRequest.getFirstName());
                user1.setSecondName(signUpRequest.getSecondName());
                user1.setGender(signUpRequest.getGender());
                user1.setDateOfBirth(signUpRequest.getDateOfBirth());
                user1.setEmergencyContact1(null);
                user1.setEmergencyContact2(null);

                user1.setAccountType("USER");
                log.info("Registering new Mobile User:  Id:" + user1.getPhone());

                user1.setVerified(false);
                user1.setBlocked(false);
                user1.setDeleted(false);

                user1 = accountRepository.save(user1);
                signUpResponse.setMessage("successfully created");

                UserRoles role = new UserRoles();
                role.setAssignedBy("DEFAULT");
                role.setRole("ROLE_USER");
                role.setUserId(user1.getPhone());
                userRolesRepo.save(role);
                log.info("Assigned Default User Role to UserId:" + user1.getPhone());

                signUpResponse.setUser(user1);
                return ResponseEntity.ok().body(signUpResponse);
            } else {

                    signUpResponse.setMessage("User already exists sign in..");
                    return ResponseEntity.badRequest().body(signUpResponse);
                }
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
    public Map<String, Integer> getUserRating(String userId) {
        Map<String, Integer> crating = new HashMap<>();
        List<UserRating> ratings = userRatingsRepo.findAllByUserId(userId, Sort.unsorted()).stream()
                .filter(c -> c.getRating() > 0).collect(Collectors.toList());
        if (!ratings.isEmpty()) {

            int sum = ratings.stream().map(UserRating::getRating).reduce(0, (a, b) -> a + b);
            int rating = sum / ratings.size();
            crating.put("count", ratings.size());
            crating.put("rating", rating);
            return crating;
        }
        crating.put("count", 0);
        crating.put("rating", 0);
        return crating;

    }
    public List<UserRating> addUserReview(UserRating r) {
        r.setRating(0);
        userRatingsRepo.save(r);
        return userRatingsRepo.findAllByUserId(r.getUserId(), Sort.by("timestamp").descending()).stream()
                .filter(c -> c.getRating() == 0).collect(Collectors.toList());

    }
    public Map<String, Integer> addUserRatings(UserRating r) {
        if (r.getRating() > 0 && r.getRating() < 6)
            userRatingsRepo.save(r);
        return getUserRating(r.getUserId());
    }

    public List<UserRating> getUserReview(String userid) {
        return userRatingsRepo.findAllByUserId(userid, Sort.by("timestamp").descending()).stream()
                .filter(c -> c.getRating() == 0).collect(Collectors.toList());

    }
    /*
    public Product setCarAvailabilityFalse(List<Bookings> bookings) {
        bookings.stream().map(b -> {
            b.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            return b;
        }).forEach(bookingsRepo::save);
        Product product = poductRepository.findById(bookings.get(0).getCarId()).get();
        product.setBookings(bookingsService.getProductBookings(product.getId()));
        return product;
    }

    public Product setCarAvailabilityTrue(List<Bookings> bookings) {
        bookings.forEach(b -> {
            b.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            b.setCancelled(true);
            bookingsRepo.save(b);
        });

        Car car = carRepo.findById(bookings.get(0).getCarId()).get();
        car.setBookings(bookingsService.getCarBookings(car.getId()));
        return car;
    }

    public List<Bookings> getAllCarBookings(Principal principal) {
        User user = accountRepository.findOneByPhone(principal.getName());
        if (user!=null) {
            return bookingsRepo.findAllByOwnerId(user.getPhone(), Sort.by("timestamp").descending());
        }

        else
            return new ArrayList<Bookings>();

    }*/
}
