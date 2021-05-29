package com.prismhealth.Controllers;

import java.security.Principal;
import java.util.List;

import com.prismhealth.Models.Notification;
import com.prismhealth.Models.PushNotification;
import com.prismhealth.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/notification")
@CrossOrigin
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/all")
    public List<Notification> getAllUserNotifications(Principal principal) {
        return notificationService.getAllUserNotification(principal);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteNotification(@PathVariable("id") String id, Principal principal) {
        return notificationService.deleteNotification(id, principal);
    }

    @PostMapping("/send")
    public String sendPushNotification(@RequestBody PushNotification pushNotification) {
        return notificationService.sendPushNotification(pushNotification);
    }

    @PostMapping
    public Notification addUserNotification(Principal principal, @RequestBody Notification notification) {
        return notificationService.addUserNotification(principal, notification);
    }

}
