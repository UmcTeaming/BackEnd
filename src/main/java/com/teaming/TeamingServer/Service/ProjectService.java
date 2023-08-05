package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MainPageResponseDto;
import com.teaming.TeamingServer.Domain.Dto.ProjectStatusRequestDto;
import com.teaming.TeamingServer.Domain.Dto.ScheduleConfirmDto;
import com.teaming.TeamingServer.Domain.Dto.ScheduleResponseDto;
import com.teaming.TeamingServer.Domain.entity.*;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.*;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.List;
import java.util.stream.Collectors;



@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    private final MemberProjectRepository memberProjectRepository;
    private final ScheduleRepository scheduleRepository;

    public List<ScheduleResponseDto> searchSchedule(Long memberId, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 프로젝트 ID"));

        Member member = memberRepository.findById(memberId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found with id: " + memberId));
        // 프로젝트에 해당하는 스케줄들을 조회한다.

        // 조회한 스케줄들을 ScheduleResponseDto 형태로 변환하여 리스트에 담는다.
        List<ScheduleResponseDto> result = project.getSchedules().stream()
                .map(schedule -> new ScheduleResponseDto(schedule.getSchedule_name(), schedule.getSchedule_start(),
                 schedule.getSchedule_start_time())).collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    public List<ScheduleConfirmDto> readSchedule(Long memberId, Long projectId, Long scheduleId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 프로젝트 Id"));
        Member member = memberRepository.findById(memberId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found with id: " + memberId));
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "유효하지 않은 스케줄 Id"));

        List <ScheduleConfirmDto> result = project.getSchedules().stream()
                .map(scheduleConfirm -> new ScheduleConfirmDto(schedule.getSchedule_name(), schedule.getSchedule_start(),
                        schedule.getSchedule_end(), schedule.getSchedule_start_time(),
                        schedule.getSchedule_end_time())).collect(Collectors.toList());
        if(result.isEmpty()) {
            return null;
        }
        return result;
    }

    // 프로젝트 마감 (상태 변경)
    public ResponseEntity projectChangeStatus(ProjectStatusRequestDto projectStatusRequestDto, Long projectId) {
        Optional<Project> projects = projectRepository.findById(projectId);
        if(projects.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 프로젝트입니다."));
        }

        Project project = projects.stream().findFirst().get();

        Project result = project.updateStatus(projectStatusRequestDto.getProject_status());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "프로젝트가 종료되었습니다."));

    }

    // memberID 로 MemberProject 들 받아오기
    public List<MemberProject> findProjects(Member member) {
        return memberProjectRepository.findByMember(member);
    }


}