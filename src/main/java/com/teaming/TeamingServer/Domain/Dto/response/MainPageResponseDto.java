package com.teaming.TeamingServer.Domain.Dto.response;

import com.teaming.TeamingServer.Domain.Dto.mainPageDto.Portfolio;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.ProgressProject;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.RecentlyProject;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MainPageResponseDto {
    private Long memberId;
    private String name;
    // 최근 프로젝트
    private List<RecentlyProject> recentlyProject;
    // 진행중인 프로젝트
    private List<ProgressProject> progressProject;
    // 포트폴리오
    private List<Portfolio> portfolio;
}
