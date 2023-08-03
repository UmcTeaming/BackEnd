package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MainPageResponseDto;
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

    // memberID 로 MemberProject 들 받아오기
    public List<MemberProject> findProjects(Member member) {
        return memberProjectRepository.findByMember(member);
    }


}