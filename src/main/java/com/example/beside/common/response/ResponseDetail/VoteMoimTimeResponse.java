package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.VoteMoimTimeDto;

public class VoteMoimTimeResponse extends Response<VoteMoimTimeDto> {

    public VoteMoimTimeResponse(int httpStatusCode, String msg, VoteMoimTimeDto data) {
        super(httpStatusCode, msg, data);
    }
}