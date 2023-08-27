package com.teaming.TeamingServer.Domain.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVerificationEmailRequestDto {
    private String authentication;
}
