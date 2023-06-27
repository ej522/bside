package com.example.beside.common.response.ResponseDetail;

import java.util.List;

import com.example.beside.common.response.Response;
import com.example.beside.dto.MoimDto;

public class MoimListResponse extends Response<List<MoimDto>> {

    public MoimListResponse(int httpStatusCode, String msg, List<MoimDto> data) {
        super(httpStatusCode, msg, data);
    }
}