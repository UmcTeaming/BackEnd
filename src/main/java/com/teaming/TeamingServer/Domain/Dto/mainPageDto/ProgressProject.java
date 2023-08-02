package com.teaming.TeamingServer.Domain.Dto.mainPageDto;

import com.teaming.TeamingServer.Domain.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressProject {
    private Long projectId;
    private String projectName;
    private LocalDateTime projectStartedDate;
    private Status projectStatus;
}
