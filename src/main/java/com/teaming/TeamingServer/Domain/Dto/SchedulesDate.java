package com.teaming.TeamingServer.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchedulesDate {
    private String schedule_name;
    private LocalDate schedule_start;
    private LocalTime schedule_start_time;
    private LocalDate schedule_end;
    private LocalTime schedule_end_time;
}
