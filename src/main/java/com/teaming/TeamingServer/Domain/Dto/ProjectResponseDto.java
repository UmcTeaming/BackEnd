package com.teaming.TeamingServer.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDto {


    private String name;
    private String image;
    private LocalDate startDate;
    private LocalDate endDate;
    private String color;
    private List<MemberListDto> memberListDtos;
   // private List<PeojectScheduleResponsetDto> schedules;
}
