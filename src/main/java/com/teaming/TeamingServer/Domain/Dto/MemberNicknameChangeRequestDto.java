package com.teaming.TeamingServer.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberNicknameChangeRequestDto {
    private String changeNickName;
}
