package com.teaming.TeamingServer.Domain.Dto;

import com.teaming.TeamingServer.Domain.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusRequestDto {
    private Status project_status;
}
