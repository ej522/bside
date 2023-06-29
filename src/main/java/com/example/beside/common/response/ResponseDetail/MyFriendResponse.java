package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.FriendDto;

public class MyFriendResponse extends Response<FriendDto> {

    public MyFriendResponse(int httpStatusCode, String msg, FriendDto data) {
        super(httpStatusCode, msg, data);
    }
}