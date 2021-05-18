package com.prismhealth.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.prismhealth.Models.BlockedUser;
import com.prismhealth.Models.PushContent;
import com.prismhealth.Models.Users;
import com.prismhealth.Models.UserRating;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.BlockedUserRepo;
import com.prismhealth.repository.UserRatingsRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AccountRepository usersRepo;

    private final BlockedUserRepo blockedUserRepo;

    private final AuthService authService;

    private final NotificationService notificationService;

    private final UserRatingsRepo userRatingsRepo;

    public UserService(AccountRepository usersRepo, BlockedUserRepo blockedUserRepo, AuthService authService,
                       NotificationService notificationService, UserRatingsRepo userRatingsRepo, AccountRepository usersRepo1, BlockedUserRepo blockedUserRepo1, AuthService authService1, NotificationService notificationService1, UserRatingsRepo userRatingsRepo1){

        this.usersRepo = usersRepo1;
        this.blockedUserRepo = blockedUserRepo1;
        this.authService = authService1;
        this.notificationService = notificationService1;
        this.userRatingsRepo = userRatingsRepo1;
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
        // send user verification;
        // notificationService.carReviewNotification(r);
        return userRatingsRepo.findAllByUserId(r.getUserId(), Sort.by("timestamp").descending()).stream()
                .filter(c -> c.getRating() == 0).collect(Collectors.toList());

    }

    public Users getUserById(String phone) {

        Users users = usersRepo.findOneByPhone(phone);
        if (users !=null)
            return users;
                    else
                        return null;

    }

    public Users addUserDeviceToken(String token, Principal principal) {
        Optional<Users> optional = Optional.ofNullable(usersRepo.findOneByPhone(principal.getName()));
        if (optional.isPresent()) {
            Users users = optional.get();
            users.setDeviceToken(token);
            return usersRepo.save(users);
        } else
            return null;

    }


    public boolean deleteUser(String id, Principal principal) {
        Optional<Users> optional = Optional.ofNullable(usersRepo.findOneByPhone(id));
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
        Optional<Users> optional = Optional.ofNullable(usersRepo.findOneByPhone(id));
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
        Optional<Users> optional = Optional.ofNullable(usersRepo.findOneByPhone(id));
        if (optional.isPresent()) {
            Users users = optional.get();
            users.setBlocked(true);
            users.setBlockedBy(principal.getName());
            users.setBlockedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            BlockedUser blockedUser = new BlockedUser();
            blockedUser.setUser(users);
            blockedUser.setBlockedOn(users.getBlockedOn());
            blockedUser.setBlockedBy(users.getBlockedBy());
            blockedUserRepo.save(blockedUser);

            usersRepo.save(users);
            PushContent content = new PushContent("Prism-health Services", "Your account has been blocked");
            //notificationService.genericUserNotification(user.getPhone(), content);
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
            PushContent content = new PushContent("Prism-health Services", "Your account has been unblocked");
            //notificationService.genericUserNotification(user.getPhone(), content);
            return true;

        } else
            return false;

    }

    public boolean verifyUser(String id, Principal principal) {
        Users users = usersRepo.findOneByPhone(id);
        if (users !=null) {
            users.setVerified(true);
            users.setVerifiedBy(principal.getName());
            users.setVerifiedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            usersRepo.save(users);
            PushContent content = new PushContent("Prism-health Services", "Your account has been approved");
            //notificationService.genericUserNotification(user.getPhone(), content);
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

    public List<Users> getPendingVerifications() {
        return usersRepo.findByVerified(false).stream().filter(u -> authService.checkUserValidity(u))
                .collect(Collectors.toList());
    }

}
