package com.example.beside.service;

import com.example.beside.common.Exception.ExceptionDetail.NoResultListException;
import com.example.beside.domain.Alarm;
import com.example.beside.domain.AlarmInfo;
import com.example.beside.domain.Moim;
import com.example.beside.domain.User;
import com.example.beside.dto.AlarmDto;
import com.example.beside.dto.UserDto;
import com.example.beside.repository.FcmPushRepository;
import com.example.beside.repository.UserRepository;
import com.example.beside.util.Common;
import com.example.beside.util.Encrypt;
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
    private final UserRepository userRepository;
    private final Encrypt encrypt;

    public String sendFcmPushNotification(String fcmToken, String title, String body, String encrptedInfo, String type, String moim_name) throws FirebaseMessagingException {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .setToken(fcmToken)
                    .putData("encrptedInfo", encrptedInfo)
                    .putData("linkTo", type)
                    .putData("moim_name", moim_name)
                    .build();

            FirebaseMessaging.getInstance().send(message);

            return AlarmInfo.SUCCESS.name();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String sendFcmMoimIdNotification(String fcmToken, String title, String body, long moim_id, String type) {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .setToken(fcmToken)
                    .putData("moim_id", String.valueOf(moim_id))
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
    public void saveAlarmData(User sendUser, User receiveUser, Moim moim, String type, String status,
                              String error_msg, String title, String content) {
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

        //알림 메시지
        alarmInfo.setTitle(title);
        alarmInfo.setContent(content);

        fcmPushRepository.insertAlarm(alarmInfo);

    }

    //알림 조회
    public AlarmDto getAlarmTypeList(User user, String type) throws NoResultListException {
        List<Alarm> alarmList = fcmPushRepository.getAlarmListByType(user.getId(), type);

        List<AlarmDto.AlarmInfoDto> alarmInfoList = new ArrayList<>();

        if(alarmList.size()==0) {
            throw new NoResultListException("알림 목록이 없습니다.");
        } else {

            for(Alarm alarm : alarmList) {
                String img_url = "";

                if(alarm.getType().equals(AlarmInfo.ACCEPT.name()))
                    img_url = "https://moim.life/icon/moim_accept.png";
                else if(alarm.getType().equals(AlarmInfo.INVITE.name()))
                    img_url = "https://moim.life/icon/moim_invite.png";
                else if(alarm.getType().equals(AlarmInfo.CONFIRM.name()))
                    img_url = "https://moim.life/icon/moim_alarm.png";

                AlarmDto.AlarmInfoDto alarmInfo = new AlarmDto.AlarmInfoDto(alarm, img_url);

                alarmInfoList.add(alarmInfo);
            }
        }

        AlarmDto alarmDto = new AlarmDto();
        alarmDto.setAlarmInfoList(alarmInfoList);
        alarmDto.setAlarm_cnt(alarmInfoList.size());

        return  alarmDto;
    }

    //알람 상태 변경
    @Transactional
    public void updateAlarmStatus(long alarm_id, User user, String status) throws NoResultListException {
        fcmPushRepository.updateAlarmStatus(alarm_id, user.getId(), status);
    }

    @Transactional
    public void chkAlarmAgreeAndSend(User sender, long receive_id, long moim_id, String moim_name, String save_type, String fcm_type) {
        User receiver = userRepository.findUserById(receive_id);

        if(receiver.getPush_alarm()) {
            if(receiver.getFcm()!=null) {

                Moim moim = new Moim();
                moim.setId(moim_id);
                moim.setMoim_name(moim_name);

                String title = Common.getPushTitle(save_type);
                String content = Common.getPushContent(receiver.getName(), sender.getName(), moim.getMoim_name(), save_type);

                String result = sendFcmMoimIdNotification(receiver.getFcm(), title, content, moim.getId(), fcm_type);

                if(result.equals(AlarmInfo.SUCCESS.name())) {
                    saveAlarmData(sender, receiver, moim, save_type, AlarmInfo.SUCCESS.name(), null, title, content);
                } else {
                    saveAlarmData(sender, receiver, moim, save_type, AlarmInfo.ERROR.name(), result, title, content);
                }
            }
        }

    }

    @Transactional
    public void chkAlarmAgreeAndSend(User sender, long receive_id, String encrptedInfo, String moim_name, String save_type, String fcm_type) throws Exception {
        User receiver = userRepository.findUserById(receive_id);

        Moim moim = new Moim();
        moim.setId(Long.parseLong(encrypt.decrypt(encrptedInfo)));
        moim.setMoim_name(moim_name);

        if (receiver.getPush_alarm()) {
            if (receiver.getFcm() != null) {
                String title = Common.getPushTitle(save_type);
                String content = Common.getPushContent(receiver.getName(), sender.getName(), null, save_type);

                String result = sendFcmPushNotification(receiver.getFcm(), title, content, encrptedInfo, fcm_type, moim.getMoim_name());

                if(result.equals(AlarmInfo.SUCCESS.name())) {
                    //성공시
                    saveAlarmData(sender, receiver, moim, save_type, AlarmInfo.SUCCESS.name(), null, title, content);
                } else {
                    //실패시
                    saveAlarmData(sender, receiver, moim, save_type, AlarmInfo.ERROR.name(), result, title, content);
                }
            }
        }
    }

}
