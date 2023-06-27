package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.UserDto;

public class UserResponse extends Response<UserDto> {

    public UserResponse(int httpStatusCode, String msg, UserDto data) {
        super(httpStatusCode, msg, data);
    }
}