package com.example.beside.service;

import com.example.beside.common.response.Response;
import com.example.beside.domain.Alarm;
import com.example.beside.domain.Moim;
import com.example.beside.domain.User;
import com.example.beside.repository.FcmPushRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FcmPushService {

    private final FcmPushRepository fcmPushRepository;

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

    //알람 저장
    @Transactional
    public void saveAlarmData(User sendUser, User receiveUser, Moim moim, String type, String status, String error_msg) {
        Alarm alarmInfo = new Alarm();

        //모임관련 정보
        alarmInfo.setMoim_id(moim.getId());
        alarmInfo.setMoim_name(moim.getMoim_name());

        //메세지 받는 사람 정보
        alarmInfo.setUser_id(receiveUser.getId());
        alarmInfo.setUser_name(receiveUser.getName());

        //보내는 사람 정보
        if(!type.equals("confirm")) {
            alarmInfo.setFriend_id(sendUser.getId());
            alarmInfo.setFriend_name(sendUser.getName());
        }

        //알람 타입: invite(초대) / confirm(확정) / accept(수락)
        alarmInfo.setType(type);

        //알람 전송 시간
        alarmInfo.setAlarm_time(LocalDateTime.now());

        //알람 상태 초기: send(성공), error(실패)
        alarmInfo.setStatus(status);

        //에러 발생시 메세지
        alarmInfo.setError_msg(error_msg);

        fcmPushRepository.insertAlarm(alarmInfo);

    }
}
