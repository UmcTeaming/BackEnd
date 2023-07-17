package com.teaming.TeamingServer.Domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    ING("STATUS_ING"),
    END("STATUS_END"),
    STOP("STATUS_STOP"),
    NEW("STATUS_NEW");

    private final String status;
}
