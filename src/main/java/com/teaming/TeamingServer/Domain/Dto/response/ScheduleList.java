package com.teaming.TeamingServer.Domain.Dto.response;

import com.teaming.TeamingServer.Domain.Dto.response.FilteredSchedules;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleList {

    private List<FilteredSchedules> schedules;
}
