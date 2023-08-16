package com.teaming.TeamingServer.Domain.Dto;

import com.teaming.TeamingServer.Domain.entity.Status;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateRequestDto {

    private String project_name;
    private MultipartFile project_image;
    private LocalDate start_date;
    private LocalDate end_date;
    private String project_color;

}

