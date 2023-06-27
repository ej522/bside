package com.example.beside.common.response.ResponseDetail;

import java.util.List;

import com.example.beside.common.response.Response;
import com.example.beside.dto.VotingMoimDto;

public class VotingMoimResponse extends Response<List<VotingMoimDto>> {

    public VotingMoimResponse(int httpStatusCode, String msg, List<VotingMoimDto> data) {
        super(httpStatusCode, msg, data);
    }
}