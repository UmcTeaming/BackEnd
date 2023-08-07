package com.teaming.TeamingServer.Domain.Dto;

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
        private String project_image;

        @Size(max = 9, message = "Project name must be less than 10 characters")
        private String project_name;
        private LocalDate start_date;
        private LocalDate end_date;
        private String project_color;
        private List<CreateProjectInviteMemberDto> members;


    public ProjectCreateRequestDto(String projectImage) {
        project_image = projectImage;
    }
}
