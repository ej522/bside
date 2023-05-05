package com.example.beside.dto;

import com.example.beside.domain.Friend;
import com.example.beside.domain.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {
    @NotEmpty
    @Schema(description = "처음 만난 모임 ID", example = "7979")
    public Long first_moim_id;

    @NotEmpty
    @Schema(description = "친구 ID", example = "232")
    public Long friend_id;

    @NotEmpty
    @Schema(description = "친구 이름", example = "다람쥐")
    public String friend_name;

    public FriendDto(Friend friend, User user) {
        this.first_moim_id = friend.getFirst_moim_id();
        this.friend_id = friend.getMember_id();
        friend_name = user.getName();

    }
}
