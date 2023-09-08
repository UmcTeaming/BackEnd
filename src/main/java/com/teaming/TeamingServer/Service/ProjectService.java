package com.teaming.TeamingServer.Service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.teaming.TeamingServer.Domain.Dto.request.FilteringScheduleRequestDto;
import com.teaming.TeamingServer.Domain.Dto.request.ProjectInviteRequestDto;
import com.teaming.TeamingServer.Domain.Dto.request.ProjectStatusRequestDto;
import com.teaming.TeamingServer.Domain.Dto.request.ProjectCreateRequestDto;
import com.teaming.TeamingServer.Domain.Dto.response.*;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.InviteMember;
import com.teaming.TeamingServer.Domain.entity.*;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.*;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
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
    private final AwsS3Service awsS3Service;
    private final MemberScheduleRepository memberScheduleRepository;

    // 프로젝트 생성
    @Transactional
    public ProjectCreateResponseDto createProject(Long memberId, ProjectCreateRequestDto projectCreateRequestDto) {
        // memberId를 통해 Member 엔터티 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found"));

        // projectImage 저장 링크 받아오기
        String projectImage = awsS3Service.projectImageUpload(projectCreateRequestDto.getProject_image(), "projectImage/", projectCreateRequestDto.getProject_name());

        // DTO 정보를 사용하여 Project 객체 생성
        Project project = Project.builder()
                .project_name(projectCreateRequestDto.getProject_name())
                .project_image(projectImage)
                .start_date(projectCreateRequestDto.getStart_date())
                .end_date(projectCreateRequestDto.getEnd_date())
                .project_status(Status.ING)
                .project_color(projectCreateRequestDto.getProject_color())
                .build();

        // 프로젝트 객체를 데이터베이스에 저장하고, 반환된 객체를 가져옵니다.
        Project savedProject = projectRepository.save(project);

        // Member와 Project로 MemberProject 객체 생성 및 저장
        MemberProject memberProject = MemberProject.builder()
                .member(member)
                .project(project).build();
        memberProjectRepository.save(memberProject);

        return ProjectCreateResponseDto.builder()
                .project_id(savedProject.getProject_id())
                .build();
    }

    // 프로젝트 수정
    @Transactional
    public ProjectCreateResponseDto modifyProject(Long projectId, ProjectCreateRequestDto projectCreateRequestDto) {
        // projectId 를 통해 Project 엔터티 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 프로젝트입니다."));

        String projectImage = null;
        // 프로젝트 이미지를 수정할 파일이 있는 경우 - S3 저장소에서 파일을 지움
        if(project.getProject_image() != null) {
            awsS3Service.deleteFile(project.getProject_image());
            projectImage = awsS3Service.projectImageUpload(projectCreateRequestDto.getProject_image(), "projectImage/", projectCreateRequestDto.getProject_name());
        }

        if(projectImage == null) {
            projectImage = project.getProject_image();
        }

        // DTO 정보를 사용하여 Project 객체 수정
        project.modifyProject(projectCreateRequestDto.getProject_name(), projectCreateRequestDto.getStart_date(), projectCreateRequestDto.getEnd_date()
                                ,projectCreateRequestDto.getProject_color(), projectImage);


        return ProjectCreateResponseDto.builder()
                .project_id(project.getProject_id())
                .build();
    }


    public List<ScheduleResponseDto> searchSchedule(Long memberId, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 프로젝트 ID"));

        Member member = memberRepository.findById(memberId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "유효하지 않은 멤버 ID "));
        // 프로젝트에 해당하는 스케줄들을 조회한다.

        List<ScheduleResponseDto> result = project.getSchedules().stream()
                .map(schedule -> new ScheduleResponseDto(schedule.getSchedule_id(),schedule.getSchedule_name(), schedule.getSchedule_start(),
                 schedule.getSchedule_start_time(), schedule.getSchedule_end(),
                        schedule.getSchedule_end_time(), schedule.getProject().getProject_color())).collect(Collectors.toList());


        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    public ScheduleConfirmResponseDto readSchedule(Long memberId, Long projectId, Long scheduleId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 프로젝트 Id"));
        Member member = memberRepository.findById(memberId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found with id: " + memberId));
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(()
                -> new BaseException(HttpStatus.NOT_FOUND.value(), "유효하지 않은 스케줄 Id"));

        ScheduleConfirmResponseDto scheduleRead = new ScheduleConfirmResponseDto(
                schedule.getSchedule_name(), schedule.getSchedule_start(),
                schedule.getSchedule_start_time(), schedule.getSchedule_end(),
                schedule.getSchedule_end_time()
        );

        return scheduleRead;
    }

    // 프로젝트 마감 (상태 변경)
    @Transactional
    public ResponseEntity projectChangeStatus(ProjectStatusRequestDto projectStatusRequestDto, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(404, "유효하지 않은 프로젝트 Id"));

        project.updateStatus(projectStatusRequestDto.getProject_status());

        // 아직 프로젝트가 시작하지 않았다면, 프로젝트 시작 날짜를 오늘 날짜로 설정
        if(project.getStart_date().isAfter(LocalDate.now())) {
            project.updateStartDate(LocalDate.now());
        }

        // 마감 버튼 누른 당일로 endDate 변경
        project.updateEndDate(LocalDate.now());

        ProjectStatusResponse projectStatusResponse = ProjectStatusResponse
                .builder().startDate(project.getStart_date())
                .endDate(project.getEnd_date()).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<ProjectStatusResponse>(HttpStatus.OK.value(), "프로젝트가 종료되었습니다.", projectStatusResponse));

    }

    // 프로젝트 초대 기능
    @Transactional
    public ResponseEntity inviteMember(ProjectInviteRequestDto projectInviteRequestDto, Long projectId) {
        String email = projectInviteRequestDto.getEmail();

        // 멤버가 존재 하는지 조회
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new BaseException(HttpStatus.NOT_FOUND.value(), "회원이 아닌 초대자 입니다."));

        // 프로젝트가 존재하는지 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "존재하지 않는 프로젝트 입니다."));


        // 프로젝트로 저장 전에 이미 이 프로젝트에 참여 중인지 확인
        List<MemberProject> memberProjects = project.getMemberProjects();

        List<Member> members = memberProjects.stream()
                .map(MemberProject::getMember)
                .filter(m -> m.equals(member)) // 초대자가 이 프로젝트에 있는지
                .collect(Collectors.toList());

        if(!members.isEmpty()) {
            throw new BaseException(HttpStatus.ALREADY_REPORTED.value(), "이미 참여 중인 초대자입니다.");
        }

        // Member 초대 - MemberProject 에 찾은 멤버 추가하기
        MemberProject memberProject = MemberProject.builder()
                .member(member)
                .project(project)
                .build();

        memberProjectRepository.save(memberProject); // 프로젝트에 참여하는 member 로 매핑 후 저장

        // 초대 후 반환할 멤버 추가
        memberProjects.add(memberProject);

        // 기존에 있던 프로젝트 스케줄 초대된 회원과 매핑하기
        List<Schedule> schedules = project.getSchedules().stream().collect(Collectors.toList());

        // MemberSchedule 저장
        schedules.stream().forEach(schedule -> memberScheduleRepository.save(new MemberSchedule(member, schedule)));


        List<InviteMember> inviteMembers = memberProjects.stream()
                .map(MemberProject::getMember)
                .map(Member::toInviteMember)
                .collect(Collectors.toList());

        ProjectInviteResponseDto projectInviteResponseDto = ProjectInviteResponseDto.builder()
                                                            .members(inviteMembers).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<ProjectInviteResponseDto>(HttpStatus.OK.value(), "초대가 완료되었습니다.", projectInviteResponseDto));


    }

    //프로젝트 정보 조회
    public ProjectResponseDto getProject(Long memberId,Long projectId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Project not found"));

        List<MemberListDto> memberListDtos = project.getMemberProjects().stream()
                .map(memberProject -> {
                    Member memberInProject = memberProject.getMember();
                    return MemberListDto.builder()
                            .member_name(memberInProject.getName())
                            .member_image(memberInProject.getProfile_image())
                            .email(memberInProject.getEmail())
                            .build();
                })
                .collect(Collectors.toList());


        ProjectResponseDto projectResponseDto = ProjectResponseDto.builder()
                .name(project.getProject_name())
                .image(project.getProject_image())
                .startDate(project.getStart_date())
                .endDate(project.getEnd_date())
                .projectStatus(project.getProject_status())
                .projectColor(project.getProject_color())
                .memberListDtos(memberListDtos)
                .build();

        return projectResponseDto;
    }


    // 프로젝트 삭제
    public void deleteProject(Long memberId, Long projectId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Member not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), "Project not found"));
        
       List<File> filesToDelete = project.getFiles();

        if (project.getProject_image() != null) {
            awsS3Service.deleteFile(project.getProject_image());
        }
       awsS3Service.deleteProjectFiles(filesToDelete);
       projectRepository.delete(project);
    }

}