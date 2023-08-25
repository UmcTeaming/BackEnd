package com.teaming.TeamingServer.Domain.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectStatusResponse {
    private LocalDate startDate;
    private LocalDate endDate;
}
