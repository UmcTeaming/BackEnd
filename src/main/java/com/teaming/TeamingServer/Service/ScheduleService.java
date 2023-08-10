package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Domain.entity.*;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.ScheduleRepository;
import com.teaming.TeamingServer.Repository.ProjectRepository;
import com.teaming.TeamingServer.Repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    public void generateSchedule(Long memberId, Long projectId, ScheduleEnrollRequestDto scheduleEnrollRequestDto) {
        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Project not found"));

        Schedule schedule = Schedule.builder()
                .schedule_name(scheduleEnrollRequestDto.getSchedule_name())   // 스케줄 이름 설정
                .scheduleStart(scheduleEnrollRequestDto.getSchedule_start())   // 스케줄시작날짜
                .schedule_start_time(scheduleEnrollRequestDto.getSchedule_start_time())    // 스케줄시작시간
                .schedule_end(scheduleEnrollRequestDto.getSchedule_end())    // 스케줄끝날짜
                .schedule_end_time(scheduleEnrollRequestDto.getSchedule_end_time())   // 스케줄끝시간
                .project(project)  // 프로젝트와 스케줄 연결
                .build();

        // 스케줄을 데이터베이스에 저장한다.
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long memberId, Long projectId, Long scheduleId) {
        // 스케줄을 삭제하기 전에 해당 스케줄이 속한 프로젝트와 프로젝트에 해당하는 스케줄인지, 어떤 멤버가 연관되어있는지 확인해야 한다.
        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Project not found"));
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Schedule not found"));
        Member member = memberRepository.findById(memberId).orElseThrow(()
                -> new EntityNotFoundException("Member not found"));
        // 프로젝트와 스케줄, 멤버가 연관되어 있는지 확인한다.

        if (!schedule.getProject().equals(project)) {
            throw new IllegalArgumentException("Schedule does not belong to the specified project.");
        }
        // 스케줄을 삭제한다.
        scheduleRepository.deleteById(scheduleId);
    }


//    public void readSchedule(Long memberId, Long projectId, Long scheduleId) {
//        Project project = projectRepository.findById(projectId).orElseThrow(()
//                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Project not found"));
//        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(()
//                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Schedule not found"));
//        Member member = memberRepository.findById(memberId).orElseThrow(()
//                -> new EntityNotFoundException("Member not found"));
//
//        if(!schedule.getProject().equals(project)) {
//            throw new IllegalArgumentException("Schedule does not belong to the specified project.");
//        }
//        if(!schedule.getMembersSchedules().equals(member)) {
//            throw new IllegalArgumentException("Schedule does not belong to the specified member.");
//        }
//
//    }


}
