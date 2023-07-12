package com.example.beside.service;

import com.example.beside.common.Exception.ExceptionDetail.NoResultListException;
import com.example.beside.domain.Alarm;
import com.example.beside.domain.AlarmInfo;
import com.example.beside.domain.Moim;
import com.example.beside.domain.User;
import com.example.beside.dto.AlarmDto;
import com.example.beside.repository.FcmPushRepository;
import com.example.beside.util.Common;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

            return AlarmInfo.SUCCESS.name();
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
        alarmInfo.setReceive_id(receiveUser.getId());
        alarmInfo.setReceive_name(receiveUser.getName());

        //보내는 사람 정보
        if(!type.equals(AlarmInfo.CONFIRM.name())) {
            alarmInfo.setSend_id(sendUser.getId());
            alarmInfo.setSend_name(sendUser.getName());
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

    //알람 조회
    public List<AlarmDto> getAlarmTypeList(User user, String type) throws NoResultListException {
        List<Alarm> alarmList = fcmPushRepository.getAlarmListByType(user.getId(), type);

        List<AlarmDto> alarmInfoList = new ArrayList<>();

        if(alarmList.size()==0) {
            throw new NoResultListException("알람 목록이 없습니다.");
        } else {
            for(Alarm alarm : alarmList) {
                String title = Common.getPushTitle(alarm.getType());
                String content = Common.getPushContent(alarm.getReceive_name(), alarm.getSend_name(), alarm.getMoim_name(), alarm.getType());

                AlarmDto alarmInfo = new AlarmDto(alarm, title, content);

                alarmInfoList.add(alarmInfo);
            }
        }

        return  alarmInfoList;
    }

    //알람 상태 변경
    @Transactional
    public void updateAlarmStatus(long alarm_id, User user, String status) throws NoResultListException {
        fcmPushRepository.updateAlarmStatus(alarm_id, user.getId(), status);
    }
}
