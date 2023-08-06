package com.teaming.TeamingServer.Domain.Dto.mainPageDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDto {
    private Long member_id;
    private Long project_id;
    private Long schedule_id;
}
