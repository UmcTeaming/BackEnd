package com.teaming.TeamingServer.Domain.Dto.mainPageDto;

import com.teaming.TeamingServer.Domain.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentlyProject {
    private Long projectId;
    private String projectName;
    private LocalDate projectCreatedDate;
    private Status projectStatus;
    private String projectImage;
}
