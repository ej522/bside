package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.AlarmDto;

public class AlarmInfoResponse extends Response<AlarmDto> {
    public AlarmInfoResponse(int httpStatusCode, String msg, AlarmDto data) {
        super(httpStatusCode, msg, data);
    }
}
