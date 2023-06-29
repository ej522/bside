package com.example.beside.common.response.ResponseDetail;

import com.example.beside.common.response.Response;

public class BooleanResponse extends Response<Boolean> {

    public BooleanResponse(int httpStatusCode, String msg, Boolean data) {
        super(httpStatusCode, msg, data);
    }
}