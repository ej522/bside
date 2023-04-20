package com.example.beside.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.beside.domain.Moim;
import com.example.beside.dto.MoimOveralScheduleDto;
import com.example.beside.repository.MoimRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component
public class Scheduler {

    private final MoimRepository moimRepository;

    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 30 * * * *")
    public void fixMoimSchedulering() {
        // 일정이 확정되지 않은 모임 조회
        List<Moim> notFixedScheduleMoims = moimRepository.getNotFixedScheduleMoims();

        for (var moim : notFixedScheduleMoims) {
            // 해당 모임의 종합 정보 조회
            List<MoimOveralScheduleDto> moimScheduleInfo = moimRepository.getMoimScheduleInfo(moim.getId());

            var created_time = moimScheduleInfo.get(0).getCreated_time();
            var dead_line_hour = moimScheduleInfo.get(0).getDead_line_hour();

            LocalDateTime standardTime = created_time.plusHours(dead_line_hour);
            if (LocalDateTime.now().isAfter(standardTime)) {
                // 일정 정하기
                for (var moimInfo : moimScheduleInfo) {
                }
            }

        }
        moimRepository.getMoimScheduleInfo(0);
    }
}
