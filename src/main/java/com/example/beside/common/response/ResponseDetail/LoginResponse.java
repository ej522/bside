package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.UserTokenDto;

public class LoginResponse extends Response<UserTokenDto> {

    public LoginResponse(int httpStatusCode, String msg, UserTokenDto data) {
        super(httpStatusCode, msg, data);
    }
}