package com.teaming.TeamingServer.Domain.Dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleConfirmResponseDto {
    private String schedule_name;
    private LocalDate schedule_start;
    private LocalTime schedule_start_time;
    private LocalDate schedule_end;
    private LocalTime schedule_end_time;
}
