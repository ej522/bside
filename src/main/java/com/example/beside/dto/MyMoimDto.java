package com.example.beside.dto;

import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimMember;
import com.example.beside.domain.User;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyMoimDto {
    private Long moim_id;
    private String moim_name;
    private String host_profile_img;
    private String fixed_date;
    private String fixed_time;
    private Long memeber_cnt;

    public MyMoimDto(Moim moim, User user, Long memeber_cnt) {
        this.moim_id = moim.getId();
        this.moim_name = moim.getMoim_name();
        this.host_profile_img = user.getProfile_image();
        this.fixed_date = moim.getFixed_date();
        this.fixed_time = moim.getFixed_time();
        this.memeber_cnt = memeber_cnt;
    }

}
