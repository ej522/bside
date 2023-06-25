package com.example.beside.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FcmPushService {

    public void sendFcmPushNotification(String fcmToken, String title, String body, String encrptedInfo, String type) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .setToken(fcmToken)
                .putData("encrptedInfo", encrptedInfo)
                .putData("linkTo", type)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent message: " + response);
    }
}
