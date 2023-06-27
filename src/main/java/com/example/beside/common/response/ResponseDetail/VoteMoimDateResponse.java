package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;
import com.example.beside.dto.VoteMoimDateDto;

import java.util.List;

public class VoteMoimDateResponse extends Response<List<VoteMoimDateDto>> {

    public VoteMoimDateResponse(int httpStatusCode, String msg, List<VoteMoimDateDto> data) {
        super(httpStatusCode, msg, data);
    }
}