package com.teaming.TeamingServer.Domain.Dto;

import com.teaming.TeamingServer.Domain.entity.Status;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class ProjectCreateRequestDto {

    private String project_name;
    private String project_image;
    private LocalDate start_date;
    private LocalDate end_date;
    private String project_color;

}

