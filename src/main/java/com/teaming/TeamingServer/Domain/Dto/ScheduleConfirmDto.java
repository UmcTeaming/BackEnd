package com.teaming.TeamingServer.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class ScheduleConfirmDto {
    private String schedule_name;
    private LocalDate schedule_start;
    private LocalDate schedule_end;
    private LocalTime schedule_start_time;
    private LocalTime schedule_end_time;
}
