package com.teaming.TeamingServer.common;

import lombok.Getter;

@Getter
public class BaseResponse<T> {

    private final int status;
    private String message; // 응답에 메시지가 포함 안될 수도 있으므로 final 뺌
    private T data; // 응답에 데이터가 포함 안될 수도 있으므로 final 뺌

    // status, message, data 모두 넘겨주는 Response
    public BaseResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // status, message 만 넘겨주는 Response
    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    // status, data 만 넘겨주는 Response
    public BaseResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }
}
