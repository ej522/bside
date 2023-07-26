package com.example.beside.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.beside.domain.User;
import com.google.firebase.messaging.FirebaseMessagingException;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class FcmPushServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private FcmPushService fcmPushService;

//     @Test
//     @DisplayName("FCM 토큰으로 push 알람 보내기")
//     void testSendFcmPushNotification() throws FirebaseMessagingException {
//     Long user_id = (long) 20452;
//     User findUserById = userService.findUserById(user_id);
//
//     //String fcm = findUserById.getFcm();
//     String fcm = "cJPSxZoFQSCZcxT0_iZSmB:APA91bFKRB-QBbKh1Bc2A-WYMa_L2l_b6IYY4Dt8pE7iQsWbOuwApNeFZsNv2Dhpskwx_Xv32b104FcpJYNoujZLQUdQT1p__xU_Rwl5eZjUNMouSSl_fXHN3rRWsM77M4ngWTLR5t75";
//
//     fcmPushService.sendFcmPushNotification(fcm, "hello", "how are you", "LT8+thMVlKTpGc+Iti+pNw==", "test");
//     }
}
