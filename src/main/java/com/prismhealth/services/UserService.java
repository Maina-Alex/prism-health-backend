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
import com.prismhealth.Models.User;
import com.prismhealth.Models.UserRating;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.BlockedUserRepo;
import com.prismhealth.repository.UserRatingsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private AccountRepository usersRepo;
    @Autowired
    private BlockedUserRepo blockedUserRepo;
    @Autowired
    private AuthService authService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRatingsRepo userRatingsRepo;

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

    public User getUserById(String phone) {

        Optional<User> user = Optional.ofNullable(usersRepo.findOneByPhone(phone));
        if (user.isPresent())
            return user.get();
        else
            return null;

    }

    public User addUserDeviceToken(String token, Principal principal) {
        Optional<User> optional = Optional.ofNullable(usersRepo.findOneByPhone(principal.getName()));
        if (optional.isPresent()) {
            User user = optional.get();
            user.setDeviceToken(token);
            return usersRepo.save(user);
        } else
            return null;

    }


    public boolean deleteUser(String id, Principal principal) {
        Optional<User> optional = Optional.ofNullable(usersRepo.findOneByPhone(id));
        if (optional.isPresent()) {
            User user = optional.get();
            user.setDeletedBy(principal.getName());
            user.setDeletedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            user.setDeleted(true);
            usersRepo.save(user);
            return true;

        } else
            return false;
    }

    public boolean approveDeleteUser(String id, Principal principal) {
        Optional<User> optional = Optional.ofNullable(usersRepo.findOneByPhone(id));
        if (optional.isPresent()) {
            User user = optional.get();

            user.setApproveDelete(true);
            user.setApproveDeleteBy(principal.getName());
            user.setOpproveDeleteOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            usersRepo.save(user);
            return true;

        } else
            return false;

    }

    public boolean blockUser(String id, Principal principal) {
        Optional<User> optional = Optional.ofNullable(usersRepo.findOneByPhone(id));
        if (optional.isPresent()) {
            User user = optional.get();
            user.setBlocked(true);
            user.setBlockedBy(principal.getName());
            user.setBlockedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            BlockedUser blockedUser = new BlockedUser();
            blockedUser.setUser(user);
            blockedUser.setBlockedOn(user.getBlockedOn());
            blockedUser.setBlockedBy(user.getBlockedBy());
            blockedUserRepo.save(blockedUser);

            usersRepo.save(user);
            PushContent content = new PushContent("Prism-health Services", "Your account has been blocked");
            //notificationService.genericUserNotification(user.getPhone(), content);
            return true;

        } else
            return false;
    }

    public boolean unBlockUser(String id, Principal principal) {
        Optional<User> optional = usersRepo.findById(id);
        if (optional.isPresent()) {
            User user = optional.get();
            user.setBlocked(false);
            usersRepo.save(user);
            PushContent content = new PushContent("Prism-health Services", "Your account has been unblocked");
            //notificationService.genericUserNotification(user.getPhone(), content);
            return true;

        } else
            return false;

    }

    public boolean verifyUser(String id, Principal principal) {
        Optional<User> optional = Optional.ofNullable(usersRepo.findOneByPhone(id));
        if (optional.isPresent()) {
            User user = optional.get();
            user.setVerified(true);
            user.setVerifiedBy(principal.getName());
            user.setVerifiedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            usersRepo.save(user);
            PushContent content = new PushContent("Prism-health Services", "Your account has been approved");
            //notificationService.genericUserNotification(user.getPhone(), content);
            return true;

        } else
            return false;
    }

    public List<User> getBlockedUsers() {
        return usersRepo.findByBlocked(true, Sort.by("blockedOn").descending()).stream()
                .filter(u -> !u.isDeleted() && !u.isApproveDelete()).collect(Collectors.toList());

    }

    public List<User> getDeleteUser() {
        return usersRepo.findByDeletedAndApproveDelete(true, false, Sort.by("deletedOn").descending());
    }

    public List<User> getPendingVerifications() {
        return usersRepo.findByVerified(false).stream().filter(u -> authService.checkUserValidity(u))
                .collect(Collectors.toList());
    }

}
