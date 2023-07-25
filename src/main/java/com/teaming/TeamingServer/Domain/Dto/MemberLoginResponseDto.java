package com.teaming.TeamingServer.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginResponseDto {
    // JWT 대한 인증 타입으로, 여기서는 Bearer 를 사용
    // 이후 HTTP 헤더에 prefix 로 붙여주는 타입
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
