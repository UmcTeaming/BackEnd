package com.teaming.TeamingServer.Domain.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FilteringScheduleRequestDto {

    private LocalDate schedule_start;
}
