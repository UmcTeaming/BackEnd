package com.teaming.TeamingServer.Domain.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberRequestDto {
    private String name;
    private String profile_image;
}
