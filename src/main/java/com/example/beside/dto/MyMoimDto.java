package com.example.beside.dto;

import com.example.beside.domain.Moim;
import com.example.beside.domain.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyMoimDto {
    @NotEmpty
    @Schema(description = "모임 ID", example = "1")
    private Long moim_id;

    @NotEmpty
    @Schema(description = "모임 명", example = "작당모의")
    private String moim_name;

    @NotEmpty
    @Schema(description = "모임 주최자 프로필 이미지", example = "https://moim.life/profile/green.jpg")
    private String host_profile_img;

    @NotEmpty
    @Schema(description = "모임 확정 날짜", example = "2023-12-20")
    private String fixed_date;

    @NotEmpty
    @Schema(description = "모임 확정 시간", example = "14")
    private String fixed_time;

    @NotEmpty
    @Schema(description = "모임 인원", example = "6")
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
