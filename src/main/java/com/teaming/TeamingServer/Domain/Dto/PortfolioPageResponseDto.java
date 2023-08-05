package com.teaming.TeamingServer.Domain.Dto;

import com.teaming.TeamingServer.Domain.Dto.mainPageDto.Portfolio;
//import com.teaming.TeamingServer.Domain.Dto.PortfolioPageResponseDto.PortfolioPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioPageResponseDto {
    private Long member_id;
    private List<Portfolio> portfolio;
}
