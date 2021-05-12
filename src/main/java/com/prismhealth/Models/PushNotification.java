package com.prismhealth.Models;

public class PushNotification {
    private String deviceToken;
    private PushContent notification;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public PushContent getNotification() {
        return notification;
    }

    public void setNotification(PushContent notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "PushNotification [deviceToken=" + deviceToken + ", notification=" + notification + "]";
    }

}
