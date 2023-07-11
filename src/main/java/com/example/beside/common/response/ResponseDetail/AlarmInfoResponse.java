package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.AlarmDto;

import java.util.List;

public class AlarmInfoResponse extends Response<List<AlarmDto>> {
    public AlarmInfoResponse(int httpStatusCode, String msg, List<AlarmDto> data) {
        super(httpStatusCode, msg, data);
    }
}
