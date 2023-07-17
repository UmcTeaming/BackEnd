package com.teaming.TeamingServer.common;

import com.teaming.TeamingServer.Exception.BaseException;
import lombok.Getter;

@Getter
public class BaseErrorResponse {
    private final int code;
    private final String message;

    public BaseErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseErrorResponse(BaseException baseException) {
        this.code = baseException.getCode();
        this.message = baseException.getMessage();
    }
}
