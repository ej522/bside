package com.example.beside.common.response.ResponseDetail;

import java.util.List;

import com.example.beside.common.response.Response;
import com.example.beside.dto.UserDto;

public class AllUsersResponse extends Response<List<UserDto>> {

    public AllUsersResponse(int httpStatusCode, String msg, List<UserDto> data) {
        super(httpStatusCode, msg, data);
    }
}