package com.teaming.TeamingServer.Domain.Dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ScheduleResponseDto {

    private Long schedule_id;
    private String schedule_name;
    private LocalDate schedule_start;
    private LocalTime schedule_start_time;
    private LocalDate schedule_end;
    private LocalTime schedule_end_time;
}
