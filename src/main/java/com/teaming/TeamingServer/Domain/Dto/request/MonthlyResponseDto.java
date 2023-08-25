package com.teaming.TeamingServer.Domain.Dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MonthlyResponseDto {

    private LocalDate date_list;
}
