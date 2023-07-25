package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.ScheduleDto;
import com.teaming.TeamingServer.Domain.entity.Project;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
// @EnableJpaAuditing  // 이거 필요한지 어쩐지 모름
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ScheduleDto> searchSchedule(Long projectId) {

        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Project not found with id: " + projectId));

        // 프로젝트에 해당하는 스케줄들을 조회한다.

        // 조회한 스케줄들을 ScheduleDto 형태로 변환하여 리스트에 담는다.
        List<ScheduleDto> result = project.getSchedules().stream().map(schedule
                -> new ScheduleDto(schedule.getSchedule_name(), schedule.getSchedule_start(),
                schedule.getSchedule_end(), schedule.getSchedule_start_time(),
                schedule.getSchedule_end_time(), schedule.getMemo())).collect(Collectors.toList());
        return result;
    }
}
