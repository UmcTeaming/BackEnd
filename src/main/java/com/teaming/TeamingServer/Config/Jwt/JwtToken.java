package com.teaming.TeamingServer.Config.Jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtToken {
    private String grantType;
    private Long memberId;
    private String accessToken;
//    private String refreshToken;
}