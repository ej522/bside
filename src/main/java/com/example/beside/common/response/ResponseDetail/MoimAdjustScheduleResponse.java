package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.MoimAdjustScheduleDto;

public class MoimAdjustScheduleResponse extends Response<MoimAdjustScheduleDto> {

    public MoimAdjustScheduleResponse(int httpStatusCode, String msg, MoimAdjustScheduleDto data) {
        super(httpStatusCode, msg, data);
    }
}