package com.teaming.TeamingServer.Domain.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberListDto {

    private String member_name;
    private String member_image;
}
