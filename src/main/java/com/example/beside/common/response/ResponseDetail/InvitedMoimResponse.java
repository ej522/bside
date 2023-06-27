package com.example.beside.common.response.ResponseDetail;

import java.util.List;

import com.example.beside.common.response.Response;
import com.example.beside.dto.InvitedMoimListDto;

public class InvitedMoimResponse extends Response<List<InvitedMoimListDto>> {

    public InvitedMoimResponse(int httpStatusCode, String msg, List<InvitedMoimListDto> data) {
        super(httpStatusCode, msg, data);
    }
}