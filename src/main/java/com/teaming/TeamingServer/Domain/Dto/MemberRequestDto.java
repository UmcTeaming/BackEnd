package com.teaming.TeamingServer.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {
    private String name;
    private String email;
    private String password;
    private String checkPassword;
}
