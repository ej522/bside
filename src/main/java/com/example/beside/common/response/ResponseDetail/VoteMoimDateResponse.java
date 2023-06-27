package com.example.beside.common.response.ResponseDetail;

import java.util.List;

import com.example.beside.common.response.Response;
import com.example.beside.dto.VoteMoimDateDto;

public class VoteMoimDateResponse extends Response<List<VoteMoimDateDto>> {

    public VoteMoimDateResponse(int httpStatusCode, String msg, List<VoteMoimDateDto> data) {
        super(httpStatusCode, msg, data);
    }
}