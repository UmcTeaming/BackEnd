package com.teaming.TeamingServer.Domain.Dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Data
public class MonthlyRequestDto {

    private LocalDate date_request;
}
