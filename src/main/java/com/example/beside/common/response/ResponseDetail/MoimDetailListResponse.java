package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.MoimDetailDto;

public class MoimDetailListResponse extends Response<MoimDetailDto> {

    public MoimDetailListResponse(int httpStatusCode, String msg, MoimDetailDto data) {
        super(httpStatusCode, msg, data);
    }
}