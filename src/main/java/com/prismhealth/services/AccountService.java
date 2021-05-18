package com.prismhealth.services;

import com.auth0.jwt.JWT;
import com.prismhealth.Models.Users;
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
import com.prismhealth.security.SecurityConstants;
import com.prismhealth.util.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AuthService authService;
    private final UserRatingsRepo userRatingsRepo;
    private final UserRolesRepo userRolesRepo;
    @Autowired
    private BCryptPasswordEncoder encoder;


    public AccountService(AccountRepository accountRepository, AuthService authService, UserRatingsRepo userRatingsRepo, UserRolesRepo userRolesRepo){
        this.accountRepository = accountRepository;
        this.authService = authService;
        this.userRatingsRepo = userRatingsRepo;
        this.userRolesRepo = userRolesRepo;
    }
    public ResponseEntity<SignUpResponse> authentication(phone phone) {
        SignUpResponse signUpResponse = new SignUpResponse();
        Users users = accountRepository.findOneByPhone(phone.getPhone());
        if (users !=null){
            log.info("phone->"+ users.getPhone());
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

        Users thisUsers = accountRepository.findOneByPhone(signUpRequest.getPhone());
            if (thisUsers ==null) {
                Users users1 = new Users();
                users1.setPassword(encoder.encode(signUpRequest.getPassword()));
                users1.setPhone(signUpRequest.getPhone());
                users1.setEmail(signUpRequest.getEmail());
                users1.setFirstName(signUpRequest.getFirstName());
                users1.setSecondName(signUpRequest.getSecondName());
                users1.setGender(signUpRequest.getGender());
                users1.setDateOfBirth(signUpRequest.getDateOfBirth());
                users1.setLocationName(signUpRequest.getLocation());
                users1.setPosition(new double[]{Double.parseDouble(signUpRequest.getLatitude()), Double.parseDouble(signUpRequest.getLongitude())});
                users1.setEmergencyContact1(null);
                users1.setEmergencyContact2(null);
                users1.setAccountType("USER");
                log.info("Registering new Mobile User:  Id:" + users1.getPhone());

                users1.setVerified(true);
                users1.setBlocked(false);
                users1.setDeleted(false);

                users1 = accountRepository.save(users1);
                signUpResponse.setMessage("successfully created");

                UserRoles role = new UserRoles();
                role.setAssignedBy("DEFAULT");
                role.setRole("ROLE_USER");// "ROLE_ADMIN", "ROLE_HELP_SUPPORT", "ROLE_SITE_CONTENT_UPDATER"));
                role.setUserId(users1.getPhone());
                userRolesRepo.save(role);
                log.info("Assigned Default User Role to UserId:" + users1.getPhone());

                signUpResponse.setUsers(users1);
                return ResponseEntity.ok().body(signUpResponse);
            } else {

                    signUpResponse.setMessage("User already exists sign in..");
                    return ResponseEntity.badRequest().body(signUpResponse);
                }
    }

    public ResponseEntity<?> forgotPassword(String email) {
       //TODO implement the notification service to send the change password link.
        log.info("Send link to email "+email);

        return new ResponseEntity<>(authService.forgotPassword(email),HttpStatus.OK);
    }
    public String getToken(String phone) {
        Optional<Users> users = Optional.ofNullable(accountRepository.findOneByPhone(phone));
        if (users.isPresent() && authService.checkUserValidity(users.get())) {
            String token = JWT.create().withSubject(users.get().getEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                    .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
            log.info("Getting token for firebase id " + phone + " is " + LogMessage.SUCCESS);
            return token;
        } else {
            log.info("Getting token for firebase id " + phone + "  " + LogMessage.FAILED);
            return null;
        }

    }

    public ResponseEntity<?> changePassword(String phone, String password) {
        Users users;

        users = accountRepository.findOneByPhone(phone);
        if (users !=null){
            users.setPassword(password);
            return ResponseEntity.ok(accountRepository.save(users));
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
    private String extractIp(HttpServletRequest request) {
        String clientIp;
        String clientXForwardedForIp = request
                .getHeader("x-forwarded-for");
        if (nonNull(clientXForwardedForIp)) {
            clientIp = parseXForwardedHeader(clientXForwardedForIp);
        } else {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
    private String parseXForwardedHeader(String header) {
        return header.split(" *, *")[0];
    }

    public ResponseEntity<SignUpResponse> updateUser(SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse =new SignUpResponse();
        if (accountRepository.existsByPhone(signUpRequest.getPhone())){
            Users user = accountRepository.findOneByPhone(signUpRequest.getPhone());
            signUpResponse.setMessage("successfully updated");
            signUpResponse.setUsers( accountRepository.insert(user));
            return ResponseEntity.ok(signUpResponse);
        }
        signUpResponse.setMessage("Failed update, User not found");
        return ResponseEntity.badRequest().body(signUpResponse);
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
