package com.teaming.TeamingServer.Domain.Dto;

import com.teaming.TeamingServer.Domain.Dto.mainPageDto.ProgressProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProgressProjectsPageResponseDto {
    private Long member_id;
    private String member_name;
    private List<ProgressProject> progressProjects;
}
