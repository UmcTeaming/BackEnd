package com.teaming.TeamingServer.Domain.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyResponseDto {

    private LocalDate date_list;
}
