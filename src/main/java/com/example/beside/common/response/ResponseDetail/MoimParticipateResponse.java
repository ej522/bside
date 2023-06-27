package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.MoimParticipateInfoDto;

public class MoimParticipateResponse extends Response<MoimParticipateInfoDto> {

    public MoimParticipateResponse(int httpStatusCode, String msg, MoimParticipateInfoDto data) {
        super(httpStatusCode, msg, data);
    }
}