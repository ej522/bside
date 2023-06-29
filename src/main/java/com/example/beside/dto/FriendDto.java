package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {

    @Schema(description = "총 친구수", example = "1")
    private int friend_cnt;

    private List<FriendInfo> friendInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FriendInfo {
        @NotEmpty
        @Schema(description = "처음 만난 모임 ID", example = "7979")
        private Long first_moim_id;

        @NotEmpty
        @Schema(description = "친구 ID", example = "232")
        private Long friend_id;

        @NotEmpty
        @Schema(description = "친구 이름", example = "다람쥐")
        private String friend_name;

        @Schema(description = "친구 프로필", example = "https://moim.life/profile/green.jpg")
        private String profile_image;

    }

}
