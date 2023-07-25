package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.ScheduleDto;
import com.teaming.TeamingServer.Domain.entity.Schedule;
import com.teaming.TeamingServer.Domain.entity.Project;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.ScheduleRepository;
import com.teaming.TeamingServer.Repository.ProjectRepository;
import com.teaming.TeamingServer.Repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ProjectRepository projectRepository;
//    private final MemberRepository memberRepository;   // 근데 이건 좀 헷갈리긴 함

    public void generateSchedule(Long projectId, Long scheduleId, ScheduleDto scheduleDto) {
        // 요청으로부터 프로젝트의 스케줄을 가져온다.
        String schedule_name = scheduleDto.getSchedule_name();

        // 프로젝트와 멤버를 데이터베이스에서 조회한다.
        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new EntityNotFoundException("Project not found with id: " + projectId));
//        Member member = memberRepository.findById(memberId).orElseThrow(()
//                -> new EntityNotFoundException("Member not found with id:" + memberId));

        Schedule schedule = Schedule.builder()
                .schedule_name(project.getProject_name())   // 스케줄 이름 설정 - 이게 맞나...
                .schedule_start(scheduleDto.getSchedule_start())   // 스케줄시작날짜
                .schedule_start_time(scheduleDto.getSchedule_start_time())    // 스케줄시작시간
                .schedule_end(scheduleDto.getSchedule_end())    // 스케줄끝날짜
                .schedule_end_time(scheduleDto.getSchedule_end_time())   // 스케줄끝시간
                .memo(scheduleDto.getMemo())
                .project(project)   // 프로젝트와 스케줄 연결
                .build();

        // 스케줄을 데이터베이스에 저장한다.
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long projectId, Long scheduleId) {
        // 스케줄을 삭제하기 전에 해당 스케줄이 속한 프로젝트와 프로젝트에 해당하는 스케줄인지 확인해야 한다.
        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new EntityNotFoundException("Project not found with id: " + projectId));
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(()
                -> new EntityNotFoundException("Schedule not found with id: " + scheduleId));
        // 프로젝트와 스케줄이 연관되어 있는지 확인한다.

        if (!schedule.getProject().equals(project)) {
            throw new IllegalArgumentException("Schedule does not belong to the specified file.");
        }
        // 스케줄을 삭제한다.
        scheduleRepository.deleteById(projectId);
    }
}
