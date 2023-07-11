package com.example.beside.dto;

import com.example.beside.domain.Alarm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDto {

    @Schema(description = "푸쉬 아이디", example = "1")
    private long push_id;

    @Schema(description = "알람받은시각", example = "2023-07-10 22:09:03.007665")
    private LocalDateTime alarm_time;

    @Schema(description = "제목", example = "알람제목")
    private String title;

    @Schema(description = "내용", example = "알람내용")
    private String content;

    @Schema(description = "알람상태", example = "send")
    private String status;

    @Schema(description = "유저 아이디", example = "1")
    private long user_id;

    @Schema(description = "모임 아이디", example = "1")
    private long moim_id;

    public AlarmDto(Alarm alarm, String title, String content) {
        this.push_id = alarm.getId();
        this.alarm_time = alarm.getAlarm_time();
        this.title = title;
        this.content = content;
        this.status = alarm.getStatus();
        this.user_id = alarm.getReceive_id();
        this.moim_id = alarm.getMoim_id();
    }
}
