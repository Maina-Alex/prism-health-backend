package com.prismhealth.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import com.prismhealth.Models.*;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.NotificationRepo;
import com.prismhealth.repository.ProductsRepository;
import com.prismhealth.util.AppConstants;
import com.prismhealth.util.LogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    private final NotificationRepo notificationRepo;
    private final AccountRepository usersRepo;
    private final ProductsRepository productsRepository;
    @Autowired
    private RestTemplate restTemplate;

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final ExecutorService executorService;
    public NotificationService(NotificationRepo notificationRepo, AccountRepository usersRepo, ProductsRepository productsRepository, ExecutorService executorService){
        this.notificationRepo = notificationRepo;
        this.usersRepo = usersRepo;
        this.productsRepository = productsRepository;
        this.executorService = executorService;
    }

    public Notification addUserNotification(Principal principal, Notification notification) {
        Optional<User> user = Optional.ofNullable(usersRepo.findOneByPhone(principal.getName()));
        if (user.isPresent()) {
            log.info("New user notification added for user " + user.get().getPhone());
            notification.setUserId(user.get().getPhone());
            notification.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            return notificationRepo.save(notification);

        }
        return null;

    }

    public List<Notification> getAllUserNotification(Principal principal) {
        Optional<User> user = Optional.ofNullable(usersRepo.findOneByPhone(principal.getName()));

        if (user.isPresent()) {
            return notificationRepo.findAllByUserId(user.get().getPhone());
        } else
            return new ArrayList<>();

    }

    public String sendEmailNotification(String url, EmailData emailData) {
        log.info("Sending email notification Url " + url + " " + "\n Email payload : " + emailData);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EmailData> requestEntity = new HttpEntity<>(emailData, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Email notification successfully sent to Url " + url + " " + "\n Email payload : " + emailData);
            return responseEntity.getBody();

        } else {
            log.warn("Sending email notification Url " + url + " " + "\n Email payload : " + emailData + " "
                    + LogMessage.FAILED);
            return null;
        }

    }

    public String sendPushNotification(PushNotification pushNotification) {
        log.info("Sending Push notification payload : " + pushNotification);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PushNotification> requestEntity = new HttpEntity<>(pushNotification, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(AppConstants.pushNotificationUrl,
                requestEntity, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Sending Push notification payload : " + pushNotification + " " + LogMessage.SUCCESS);
            return responseEntity.getBody();

        } else {
            log.warn("Sending Push notification payload : " + pushNotification + " " + LogMessage.FAILED);
            return null;
        }

    }

    public void sendPickCarConfirmEmail(EmailData emailData) {
        Runnable task = () -> {
            sendEmailNotification(AppConstants.notificationUrl + "/product/confirm/pick", emailData);

        };

        executorService.execute(task);

    }

    public void sendReturnCarEmail(EmailData emailData) {
        Runnable task = () -> {
            sendEmailNotification(AppConstants.notificationUrl + "/product/return", emailData);

        };

        executorService.execute(task);

    }

    public void sendPickUpLocationEmail(EmailData emailData) {
        Runnable task = () -> {
            sendEmailNotification(AppConstants.notificationUrl + "/product/pickup", emailData);

        };

        executorService.execute(task);

    }

    public void sendPaymentSuccessEmail(EmailData emailData) {
        Runnable task = () -> {
            sendEmailNotification(AppConstants.notificationUrl + "/payment/success", emailData);

        };

        executorService.execute(task);

    }
    public void addHelpNotification(String userid) {
        Runnable task = () -> {
            User owner = usersRepo.findById(userid).get();
            Optional<String> deviceToken = Optional.ofNullable(owner.getAuth());
            if (deviceToken.isPresent()) {
                PushNotification notification = new PushNotification();
                notification.setDeviceToken(deviceToken.get());
                notification.setNotification(new PushContent("Prism-Health Help Request",
                        "Your Help Request has been received successfully,We will get back to you ASAP"));
                Notification n = new Notification();
                n.setAction("Push Notification");
                n.setMessage(notification.getNotification().getBody());
                n.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                n.setTitle(notification.getNotification().getTitle());
                n.setUserId(owner.getPhone());
                notificationRepo.save(n);
                this.sendPushNotification(notification);
            }

        };

        executorService.execute(task);

    }

    public void addHelpReplyNotification(String userid) {
        Runnable task = () -> {
            User owner = usersRepo.findById(userid).get();
            Optional<String> deviceToken = Optional.ofNullable(owner.getAuth());
            if (deviceToken.isPresent()) {
                PushNotification notification = new PushNotification();
                notification.setDeviceToken(deviceToken.get());
                notification.setNotification(
                        new PushContent("Prism-Health Help Request", "You have a new message from Prism-Health support"));
                Notification n = new Notification();
                n.setAction("Push Notification");
                n.setMessage(notification.getNotification().getBody());
                n.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                n.setTitle(notification.getNotification().getTitle());
                n.setUserId(owner.getPhone());
                notificationRepo.save(n);
                this.sendPushNotification(notification);
            }

        };

        executorService.execute(task);

    }
/*
    public String sendSupportMessage(SupportMail mail) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SupportMail> requestEntity = new HttpEntity<>(mail, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(AppConstants.notificationUrl + "/support",
                requestEntity, String.class);
        return responseEntity.getBody();

    }

    public void carReviewNotification(CarRating carRating) {
        Runnable task = () -> {
            Car car = carRepo.findById(carRating.getCarId()).get();
            Users owner = usersRepo.findById(car.getOwnerId()).get();
            Optional<String> deviceToken = Optional.ofNullable(owner.getDeviceToken());
            if (deviceToken.isPresent()) {
                PushNotification notification = new PushNotification();
                notification.setDeviceToken(deviceToken.get());
                notification.setNotification(new PushContent("M-Gari", "Your car has a new Review"));
                Notification n = new Notification();
                n.setAction("Push Notification");
                n.setMessage(notification.getNotification().getBody());
                n.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                n.setTitle(notification.getNotification().getTitle());
                n.setUserId(owner.getId());
                notificationRepo.save(n);
                this.sendPushNotification(notification);
            }

        };

        executorService.execute(task);

    }

    public boolean deleteNotification(String id, Principal principal) {
        Optional<Notification> optional = notificationRepo.findById(id);

        if (optional.isPresent()) {
            notificationRepo.deleteById(id);
            return true;

        }
        return false;
    }

    public void addCarNotification(Product product) {

        Runnable task = () -> {

            User owner = usersRepo.findOne().filter(user -> user.getPhone().equals(product.getUser()));
            Optional<String> deviceToken = Optional.ofNullable(owner.getDeviceToken());
            if (deviceToken.isPresent()) {
                PushNotification notification = new PushNotification();
                notification.setDeviceToken(deviceToken.get());
                notification.setNotification(new PushContent("M-Gari ", "Your car was added successfully"));
                Notification n = new Notification();
                n.setAction("Push Notification");
                n.setMessage(notification.getNotification().getBody());
                n.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                n.setTitle(notification.getNotification().getTitle());
                n.setUserId(owner.getId());
                notificationRepo.save(n);
                this.sendPushNotification(notification);
            }

        };

        executorService.execute(task);

    }



    public void addOrderNotification(Order order) {
        Runnable task = () -> {
            Users owner = usersRepo.findById(order.getUserId()).get();
            Optional<String> deviceToken = Optional.ofNullable(owner.getDeviceToken());
            if (deviceToken.isPresent()) {
                PushNotification notification = new PushNotification();
                notification.setDeviceToken(deviceToken.get());
                notification.setNotification(new PushContent("M-Gari Bookings",
                        "Bookings placed successfuly, Total cost : " + order.getTotalCost()));
                Notification n = new Notification();
                n.setAction("Push Notification");
                n.setMessage(notification.getNotification().getBody());
                n.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                n.setTitle(notification.getNotification().getTitle());
                n.setUserId(owner.getId());
                notificationRepo.save(n);
                this.sendPushNotification(notification);
            }

        };

        executorService.execute(task);

    }

    public void genericUserNotification(String userid, PushContent pushContent) {
        Runnable task = () -> {
            Users owner = usersRepo.findById(userid).get();
            Optional<String> deviceToken = Optional.ofNullable(owner.getDeviceToken());
            if (deviceToken.isPresent()) {
                PushNotification notification = new PushNotification();
                notification.setDeviceToken(deviceToken.get());
                notification.setNotification(pushContent);
                Notification n = new Notification();
                n.setAction("Push Notification");
                n.setMessage(notification.getNotification().getBody());
                n.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                n.setTitle(notification.getNotification().getTitle());
                n.setUserId(owner.getId());
                notificationRepo.save(n);
                this.sendPushNotification(notification);
            }

        };

        executorService.execute(task);

    }

    public boolean isValidEmail(String email) {
        if (email.contains("@"))
            return true;

        else
            return false;
    }
*/
}
