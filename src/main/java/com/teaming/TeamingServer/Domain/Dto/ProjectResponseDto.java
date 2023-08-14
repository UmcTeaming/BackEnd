package com.teaming.TeamingServer.Domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class ProjectResponseDto {
    private Long id;
    private String name;
    private String image;
    private LocalDate startDate;
    private LocalDate endDate;
    private String color;
//    private List<MemberImageDto> members;
    private List<ProjectFileResponseDto> files;
    private List<PeojectScheduleResponsetDto> schedules;
}
