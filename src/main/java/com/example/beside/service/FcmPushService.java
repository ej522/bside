package com.example.beside.service;

import com.example.beside.common.response.Response;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FcmPushService {

    public String sendFcmPushNotification(String fcmToken, String title, String body, String encrptedInfo, String type) throws FirebaseMessagingException {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .setToken(fcmToken)
                    .putData("encrptedInfo", encrptedInfo)
                    .putData("linkTo", type)
                    .build();

            FirebaseMessaging.getInstance().send(message);

            return "Success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
