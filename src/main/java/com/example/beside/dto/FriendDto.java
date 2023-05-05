package com.example.beside.dto;


import com.example.beside.domain.Friend;
import com.example.beside.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {
    public Long id;
    public Long user_id;
    public Long friend_id;
    public String friend_name;

    public FriendDto(Friend friend, User user) {
        this.id = friend.getId();
        this.user_id = friend.getUser().getId();
        this.friend_id = friend.getMember_id();
        friend_name = user.getName();

    }
}
