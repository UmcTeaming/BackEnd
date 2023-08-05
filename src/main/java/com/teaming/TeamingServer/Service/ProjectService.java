package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.InviteMember;
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
import org.springframework.security.core.parameters.P;
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

    // 프로젝트 초대 기능
    public ResponseEntity inviteMember(ProjectInviteRequestDto projectInviteRequestDto, Long projectId) {
        String email = projectInviteRequestDto.getEmail();

        // 멤버가 존재 하는지 조회
        List<Member> findMember = memberRepository.findByEmail(email);

        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "회원이 아닌 초대자 입니다."));
        }

        // 프로젝트가 존재하는지 조회
        Optional<Project> project = projectRepository.findById(projectId);

        if(project.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 프로젝트 입니다."));
        }


        // 프로젝트로 저장 전에 이미 이 프로젝트에 참여 중인지 확인
        List<MemberProject> resultMemberProject = memberProjectRepository.findByProject(project.stream().findFirst().get());

        if(resultMemberProject.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "프로젝트 참여자가 없습니다."));
        }

        for(int i = 0; i<resultMemberProject.size(); i++) {
            if(resultMemberProject.get(i).getMember().equals(findMember.stream().findFirst().get())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "이미 참여 중인 초대자입니다."));
            }
        }

        MemberProject memberProject = MemberProject.builder()
                .member(findMember.stream().findFirst().get())
                .project(project.stream().findFirst().get())
                .build();

        memberProjectRepository.save(memberProject); // 프로젝트에 참여하는 member 로 저장

        List<InviteMember> inviteMembers = new ArrayList<>();
        resultMemberProject = memberProjectRepository.findByProject(project.stream().findFirst().get());

        for(int i = 0; i<resultMemberProject.size(); i++) {
            // 프로젝트 참가 중인 멤버 객체
            Member member = resultMemberProject.get(i).getMember();
            InviteMember inviteMember = InviteMember.builder()
                    .member_name(member.getName())
                    .member_image(member.getProfile_image())
                    .member_email(member.getEmail()).build();

            inviteMembers.add(inviteMember);
        }

        ProjectInviteResponseDto projectInviteResponseDto = ProjectInviteResponseDto.builder()
                .members(inviteMembers).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<ProjectInviteResponseDto>(HttpStatus.OK.value(), "초대가 완료되었습니다.", projectInviteResponseDto));


    }

//    // memberID 로 MemberProject 들 받아오기
//    public List<MemberProject> findProjects(Member member) {
//        return memberProjectRepository.findByMember(member);
//    }


}