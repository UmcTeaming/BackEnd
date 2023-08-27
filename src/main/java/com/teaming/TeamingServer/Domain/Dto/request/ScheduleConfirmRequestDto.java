package com.teaming.TeamingServer.Domain.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConfirmRequestDto {
    private LocalDate schedule_start;
}
