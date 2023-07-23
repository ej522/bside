package com.example.beside.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.beside.domain.AlarmInfo;
import com.example.beside.domain.MoimMember;
import com.example.beside.domain.User;
import com.example.beside.service.FcmPushService;
import com.example.beside.service.UserService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.beside.domain.Moim;
import com.example.beside.dto.MoimOveralScheduleDto;
import com.example.beside.repository.MoimRepositoryImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component
public class Scheduler {

    private final MoimRepositoryImpl moimRepository;
    private final UserService userService;
    private final FcmPushService fcmPushService;

    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 */15 * * * *")
    public void fixMoimSchedulering() throws FirebaseMessagingException {
        // 일정이 확정되지 않은 모임 조회
        List<Moim> notFixedScheduleMoims = moimRepository.getNotFixedMoims();

        for (var moim : notFixedScheduleMoims) {
            // 모임 정보 조회
            List<MoimOveralScheduleDto> moimInfo = moimRepository.getMoimScheduleInfo(moim.getId());

            if(moimInfo.size()==0) continue;

            var created_time = moimInfo.get(0).getCreated_time();
            var dead_line_hour = moimInfo.get(0).getDead_line_hour();
            var standardTime = created_time.plusHours(dead_line_hour);

            // 데드라인 지나지 않은 모임 또는 투표한 사람이 없는 모임
            if (LocalDateTime.now().isBefore(standardTime) || moim.getNobody_schedule_selected())
                continue;

            // 최다 선택일 구하기
            List<LocalDateTime> maxSelectedDate = getMaxSelectedDate(moimInfo);

            // 동일 최다 선택일 중 우선 순위 날짜
            LocalDateTime fixedDate = getPriorityDate(maxSelectedDate);

            // 최다 시간 구하기
            List<Integer> maxSelectedTime = getMaxSelectedTime(moimInfo, fixedDate);
            // 동일 최다 시간 중 우선 순위 시간
            int fixedTime = getPriorityTime(maxSelectedTime);

            moimRepository.fixMoimDate(moim, fixedDate, fixedTime);

            String type = AlarmInfo.CONFIRM.name();

            //주최자
            User host = userService.chkPushAgree(moim.getUser().getId());
            sendFixMoimMessage(host, moim, moim.getEncrypted_id(), type);

            //참여자
            List<MoimMember> moimMemberList = moim.getMoim_member();
            for(MoimMember moimMember : moimMemberList) {
                User guest = userService.chkPushAgree(moimMember.getUser_id());
                sendFixMoimMessage(guest, moim, moim.getEncrypted_id(), type);
            }
        }

    }

    @Scheduled(cron = "0 0 1 * * *")
    public void deleteNotFixedMoim() {
        // 일정이 확정되지 않은 모임 조회
        List<Moim> notFixedScheduleMoims = moimRepository.getNotFixedMoims();

        for (var moim : notFixedScheduleMoims) {

            var created_time = moim.getCreated_time();
            var dead_line_hour = moim.getDead_line_hour();
            var standardTime = created_time.plusHours(dead_line_hour);

            // 데드라인이 지나고 투표한 사람이 없을 때
            if (LocalDateTime.now().isAfter(standardTime) && moim.getNobody_schedule_selected()) {
                moimRepository.deleteMoim(moim.getId());
            }
        }

    }

    private List<LocalDateTime> getMaxSelectedDate(List<MoimOveralScheduleDto> moimInfo) {
        // 모임 일정 중 최다 선택 일 수 찾기
        List<LocalDateTime> selectedDateList = new ArrayList<>();
        Map<LocalDateTime, Integer> dateCountMap = new HashMap<>();

        for (var curMoim : moimInfo) {
            var selectedDate = curMoim.getSelected_date();
            if (dateCountMap.containsKey(selectedDate)) {
                dateCountMap.put(selectedDate, dateCountMap.get(selectedDate) + 1);
            } else {
                dateCountMap.put(selectedDate, 1);
            }
        }
        // 중복 최다 선택 일 수 찾기
        int maxCount = Collections.max(dateCountMap.values());
        for (Map.Entry<LocalDateTime, Integer> entry : dateCountMap.entrySet()) {
            if (entry.getValue() == maxCount) {
                selectedDateList.add(entry.getKey());
            }
        }
        return selectedDateList;
    }

    private LocalDateTime getPriorityDate(List<LocalDateTime> maxSelectedDate) {
        // 선택된 날짜중 우선순위 지정
        for (var date : maxSelectedDate) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.FRIDAY) {
                return date;
            }
            if (dayOfWeek == DayOfWeek.SATURDAY) {
                return date;
            }
            if (dayOfWeek == DayOfWeek.SUNDAY) {
                return date;
            }
        }
        return maxSelectedDate.get(0);
    }

    private List<Integer> getMaxSelectedTime(List<MoimOveralScheduleDto> moimInfo, LocalDateTime fixedDate) {
        int[] maxTime = new int[22];

        moimInfo.stream()
                .filter(dto -> dto.getSelected_date().isEqual(fixedDate))
                .forEach(t -> {
                    // 각 시간대의 빈도수를 누적하여 계산
                    maxTime[9] += t.getAm_nine() ? 1 : 0;
                    maxTime[10] += t.getAm_ten() ? 1 : 0;
                    maxTime[11] += t.getAm_eleven() ? 1 : 0;
                    maxTime[12] += t.getNoon() ? 1 : 0;
                    maxTime[13] += t.getPm_one() ? 1 : 0;
                    maxTime[14] += t.getPm_two() ? 1 : 0;
                    maxTime[15] += t.getPm_three() ? 1 : 0;
                    maxTime[16] += t.getPm_four() ? 1 : 0;
                    maxTime[17] += t.getPm_five() ? 1 : 0;
                    maxTime[18] += t.getPm_six() ? 1 : 0;
                    maxTime[19] += t.getPm_seven() ? 1 : 0;
                    maxTime[20] += t.getPm_eight() ? 1 : 0;
                    maxTime[21] += t.getPm_nine() ? 1 : 0;
                });

        int max = Arrays.stream(maxTime).max().orElse(0);
        // 가장 큰 값과 같은 값을 가진 인덱스들을 리스트로 저장
        return IntStream.range(0, maxTime.length).filter(i -> maxTime[i] == max).boxed().collect(Collectors.toList());
    }

    private int getPriorityTime(List<Integer> maxSelectedTime) {
        // 선택된 시간 중 우선순위 지정
        // 오후 2 -> 3 -> 5-> 6 -> 1 -> 12
        for (var time : maxSelectedTime) {
            if (time == 14) {
                return time;
            }
            if (time == 15) {
                return time;
            }
            if (time == 17) {
                return time;
            }
            if (time == 18) {
                return time;
            }
            if (time == 13) {
                return time;
            }
            if (time == 12) {
                return time;
            }
        }
        return maxSelectedTime.get(0);
    }

    private void sendFixMoimMessage(User user, Moim moim, String encrptedInfo, String type) throws FirebaseMessagingException {
        if(user != null) {
            if(user.getFcm()!=null) {
                String result = fcmPushService.sendFcmMoimIdNotification(user.getFcm(), Common.getPushTitle(type),
                        Common.getPushContent(null, null, moim.getMoim_name(), type),
                        moim.getId(), "MOIM_RESULT");

                if(result.equals(AlarmInfo.SUCCESS.name())) {
                    fcmPushService.saveAlarmData(null, user, moim, type, AlarmInfo.SUCCESS.name(), null);
                } else {
                    fcmPushService.saveAlarmData(null, user, moim, type, AlarmInfo.ERROR.name(), result);
                }
            }
        }
    }

}
