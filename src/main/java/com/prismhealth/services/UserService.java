package com.prismhealth.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.prismhealth.Models.*;
import com.prismhealth.dto.Request.Phone;
import com.prismhealth.repository.UserRepository;

import com.prismhealth.util.PhoneTrim;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository usersRepo;

    public ResponseEntity<?> addUserReview(UserReview r) {
        Users user = usersRepo.findByPhone(PhoneTrim.trim(r.getUserPhone()));
        if (user != null) {
            ProviderRating rating = user.getProviderRating();
            List<UserReview> reviews = rating.getRatings();
            reviews.add(r);
            double average = reviews.stream().mapToDouble(UserReview::getRating).sum();
            rating.setAverageRate(average);
            rating.setRatings(reviews);
            user.setProviderRating(rating);
            return new ResponseEntity<>(HttpStatus.OK);

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

    }

    public List<UserReview> getUserRating(String userid) {
        Users user = usersRepo.findByPhone(PhoneTrim.trim(userid));
        return user.getProviderRating().getRatings().stream().sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

    }

    public List<Notice> getUserNotifications(String phone){
        Users user= usersRepo.findByPhone(PhoneTrim.trim(phone));
        if(user!=null){
            if(user.getNotifications().getNotices()!=null)
            return user.getNotifications().getNotices();
        }
        return new ArrayList<>();
    }
    public Users getUserById(String phone) {

        Users users = usersRepo.findByPhone(PhoneTrim.trim(phone));
        if (users != null)
            return users;
        else
            return null;

    }

    public boolean deleteUser(String id, Principal principal) {
        Optional<Users> optional = Optional.ofNullable(usersRepo.findByPhone(id));
        if (optional.isPresent()) {
            Users users = optional.get();
            users.setDeletedBy(principal.getName());
            users.setDeletedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            users.setDeleted(true);
            usersRepo.save(users);
            return true;

        } else
            return false;
    }

    public boolean approveDeleteUser(String id, Principal principal) {
        Optional<Users> optional = Optional.ofNullable(usersRepo.findByPhone(id));
        if (optional.isPresent()) {
            Users users = optional.get();

            users.setApproveDelete(true);
            users.setApproveDeleteBy(principal.getName());
            users.setOpproveDeleteOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            usersRepo.save(users);
            return true;

        } else
            return false;

    }

    public boolean blockUser(String id, Principal principal) {
        Optional<Users> optional = Optional.ofNullable(usersRepo.findByPhone(id));
        if (optional.isPresent()) {
            Users users = optional.get();
            users.setBlocked(true);
            users.setBlockedBy(principal.getName());
            users.setBlockedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            usersRepo.save(users);
            return true;

        } else
            return false;
    }

    public boolean unBlockUser(String id, Principal principal) {
        Optional<Users> optional = usersRepo.findById(id);
        if (optional.isPresent()) {
            Users users = optional.get();
            users.setBlocked(false);
            usersRepo.save(users);
            return true;

        } else
            return false;

    }

    public boolean verifyUser(String id, Principal principal) {
        Users users = usersRepo.findByPhone(id);
        if (users != null) {
            users.setVerified(true);
            users.setVerifiedBy(principal.getName());
            users.setVerifiedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            usersRepo.save(users);
            return true;

        } else
            return false;
    }

    public List<Users> getBlockedUsers() {
        return usersRepo.findByBlocked(true, Sort.by("blockedOn").descending()).stream()
                .filter(u -> !u.isDeleted() && !u.isApproveDelete()).collect(Collectors.toList());

    }

    public List<Users> getDeleteUser() {
        return usersRepo.findByDeletedAndApproveDelete(true, false, Sort.by("deletedOn").descending());
    }

}
