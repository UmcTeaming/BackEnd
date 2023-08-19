package com.teaming.TeamingServer.Domain.Dto;

import com.teaming.TeamingServer.Domain.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SingleFileResponseDto {

    private String project_name;
    private String file_type;
    private String file_name;
    private String uploader;
    private LocalDate upload_date;
}
