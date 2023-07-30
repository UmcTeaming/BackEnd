package com.teaming.TeamingServer.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLogoutRequestDto {
    private String grantType;
    private Long memberId;
    private String accessToken;
    private String refreshToken;
}
