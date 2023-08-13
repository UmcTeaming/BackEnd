package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtService;
import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Config.Jwt.JwtTokenProvider;
import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.Portfolio;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.ProgressProject;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.RecentlyProject;
import com.teaming.TeamingServer.Domain.entity.*;
import com.teaming.TeamingServer.Repository.MemberProjectRepository;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.Repository.ProjectRepository;
import com.teaming.TeamingServer.Repository.ScheduleRepository;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    private final ProjectRepository projectRepository;
    private final MemberProjectRepository memberProjectRepository;

    private final JwtService jwtService;

    // 상수값들 - 메인 페이지에 반환할 프로젝들 개수들
    private final static int RECENTLY_PROJECT_NUM = 3;
    private final static int PROGRESS_PROJECT_NUM = 8;
    private final static int PORTFOLIO_PROJECT_NUM = 8;



    @Override
    @Transactional
    public ResponseEntity changePassword(Long memberId, MemberChangePasswordRequestDto memberChangePasswordRequestDto) {
        // 1. memberId 를 가진 멤버가 존재하는지 확인
        Optional<Member> findMember = memberRepository.findById(memberId);
        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다."));
        }

        // 2. 존재한다면, 비밀번호 변경 후 로그인과 똑같이 인증정보 재발급
        Authentication authentication = null;

        // (1) 비밀번호 변경
        Member member = findMember.get();

        member.updatePassword(memberChangePasswordRequestDto.getChange_password());

        try {
            // Authentication 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());

            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        }
        catch (IllegalArgumentException | AuthenticationException illegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), illegalArgumentException.getMessage()));
        }

        // 검증된 인증 정보로 JWT 토큰 생성
        JwtToken token = jwtTokenProvider.generateToken(authentication);

        JwtToken newToken = JwtToken.builder()
                    .grantType(token.getGrantType())
                    .accessToken(token.getAccessToken())
                    .memberId(memberId)
                    .build();


        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<JwtToken>(HttpStatus.OK.value(), "비밀번호 변경이 완료되었습니다.", newToken));
    }

    @Override
    @Transactional
    public ResponseEntity checkCurrentPassword(Long memberId, CheckCurrentPasswordRequestDto checkCurrentPasswordRequestDto) {
        // 1. DB 에서 ID 로 회원 객체 조회 후 존재하는 회원인지 체크
        Optional<Member> findMember = memberRepository.findById(memberId);

        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다."));
        }

        // 2. 사용자가 입력한 비밀번호와 DB 에 있는 비밀번호가 일치하는지 확인
        String currentPassword = findMember.stream().findFirst().stream().toList().stream().findFirst().get().getPassword();

        if (!currentPassword.equals(checkCurrentPasswordRequestDto.getCurrentPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "알맞은 비밀번호를 입력해주세요."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "비밀번호가 일치합니다."));
    }

    @Override
    @Transactional
    public ResponseEntity MemberMyPage(Long memberId, String accessToken) {

        // 1. DB 에서 ID 로 회원 객체 조회 후 존재하는 회원인지 체크
        Optional<Member> findMember = memberRepository.findById(memberId);

        // 0. 인증 정보 확인
        if(!jwtService.VerifyAccess(accessToken, findMember.stream().findFirst().get())) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다."));
        }

        Member member = findMember.stream().findFirst().get();

        MemberMyPageResponseDto memberMyPageResponseDto = MemberMyPageResponseDto.builder()
                .memberId(memberId)
                .name(member.getName())
                .email(member.getEmail())
                .profileImage(member.getProfile_image()).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<MemberMyPageResponseDto>(HttpStatus.OK.value(), memberMyPageResponseDto));
    }

    @Override
    @Transactional
    public ResponseEntity changeNickName(Long memberId, MemberNicknameChangeRequestDto memberNicknameChangeRequestDto) {
        // 1. 존재하는 회원인지 조회
        Optional<Member> findMember = memberRepository.findById(memberId);

        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다."));
        }

        // 2. 바꾸려는 닉네임이 이미 존재하는지 확인
        Optional<Member> equalNickname = memberRepository.findByName(memberNicknameChangeRequestDto.getChange_nickname());

        if(!equalNickname.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "이미 사용 중인 닉네임입니다."));
        }

        // 3. 사용 가능한 닉네임이라면, 변경 후 변경 완료
        Member member = findMember.stream().findFirst().get();
        member.updateNickName(memberNicknameChangeRequestDto.getChange_nickname());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "닉네임 변경이 완료되었습니다."));
    }

    @Override
    @Transactional
    public ResponseEntity changeProfileImage(Long memberId, MemberChangeProfileImageRequestDto memberChangeProfileImageRequestDto) {
        // 1. 존재하는 회원인지 조회
        Optional<Member> findMember = memberRepository.findById(memberId);

        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다."));
        }

        // 2. member 프로필 이미지 변경
        Member member = findMember.stream().findFirst().get();
        member.updateProfileImage(memberChangeProfileImageRequestDto.getChange_image_link());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), "프로필 변경이 완료되었습니다."));
    }

    @Override
    public ResponseEntity mainPage(Long memberId) {
        // 1. 존재하는 회원인지 조회
        Optional<Member> findMember = memberRepository.findById(memberId);

        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다."));
        }
        // 2. memberId 로 프로젝트 조회
        Member member = findMember.stream().findFirst().get();
        List<MemberProject> memberProject = findMemberProject(member);

        // (1) 찾은 아예 프로젝트가 없다면 null 반환
        if(memberProject.isEmpty()) {
            MainPageResponseDto mainPageResponseDto = MainPageResponseDto.builder()
                    .memberId(memberId)
                    .name(member.getName())
                    .recentlyProject(null)
                    .progressProject(null)
                    .portfolio(null).build();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<MainPageResponseDto>(HttpStatus.OK.value(), mainPageResponseDto));
        }

        // (2) 찾은 프로젝트들이 있다면, 프로젝트 최근 시작 기준으로 정렬 - 최근 프로젝트
        List<Project> projects = new ArrayList<>();

        List<RecentlyProject> recentlyProject = searchRecentlyProject(memberProject, projects, RECENTLY_PROJECT_NUM);

        // (3) 진행중인 프로젝트 - 오름차순 정렬
        List<ProgressProject> progressProjects = searchProgressProject(memberProject, projects, PROGRESS_PROJECT_NUM);

        // (4) 끝난 프로젝트 - 내림차순 정렬 - 포트폴리오
        List<Portfolio> portfolios = searchPortPolio(memberProject, projects, PORTFOLIO_PROJECT_NUM);

        // 최종 반환 MainPageResponse 생성
        MainPageResponseDto mainPageResponseDto = MainPageResponseDto.builder()
                .memberId(memberId)
                .name(member.getName())
                .recentlyProject(recentlyProject)
                .progressProject(progressProjects)
                .portfolio(portfolios).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<MainPageResponseDto>(HttpStatus.OK.value(), mainPageResponseDto));
    }

    @Override
    public ResponseEntity portfolioPage(Long memberId) {
        // 1. 존재하는 회원인지 조회
        Optional<Member> findMember = memberRepository.findById(memberId);

        if(findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회원입니다."));
        }

        // 2. memberId 로 프로젝트 조회
        Member member = findMember.stream().findFirst().get();
        List<MemberProject> memberProject = findMemberProject(member);

        // (1) 찾은 아예 프로젝트가 없다면 null 반환
        if(memberProject.isEmpty()) {
            PortfolioPageResponseDto portfolioPageResponseDto = PortfolioPageResponseDto.builder()
                    .member_id(memberId)
                    .portfolio(null).build();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<PortfolioPageResponseDto>(HttpStatus.OK.value(), portfolioPageResponseDto));
        }

        // (2) 있다면, 끝난 순으로 project 정렬
        List<Project> projects = new ArrayList<>();
        List<Portfolio> portfolios = searchPortPolio(memberProject, projects, memberProject.size());

        // 최종 포트폴리오 페이지 넘기기
        PortfolioPageResponseDto portfolioPageResponseDto = PortfolioPageResponseDto.builder()
                .member_id(memberId)
                .portfolio(portfolios).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<PortfolioPageResponseDto>(HttpStatus.OK.value(), portfolioPageResponseDto));

    }

    // 최근 프로젝트
    private List<RecentlyProject> searchRecentlyProject(List<MemberProject> memberProject, List<Project> projects, int projectNum) {
        projects = new ArrayList<>();

        for(int i = 0; i<memberProject.size(); i++) {
            Project project = projectRepository.findById(memberProject.get(i).getProject().getProject_id()).get();
            // Status 가 ING 인 것만
            if(project.getProject_status().equals(Status.ING)) {
                projects.add(project);
            }
        }

        // 시작 날짜를 기준으로 내림차순 정렬 - 가장 최근으로 시작한 날짜
        Collections.sort(projects, new SortByStartDate().reversed());

        if(projectNum > projects.size()) {
            projectNum = projects.size();
        }

        // 내림차순 정렬한 것 RecentlyProject 형식으로 3개만 담기
        List<RecentlyProject> recentlyProject = new ArrayList<>();

        for(int i = 0; i<projectNum; i++) {
            RecentlyProject project = RecentlyProject.builder()
                    .projectId(projects.get(i).getProject_id())
                    .projectName(projects.get(i).getProject_name())
                    .projectStatus(projects.get(i).getProject_status())
                    .projectCreatedDate(projects.get(i).getStart_date())
                    .projectImage(projects.get(i).getProject_image()).build();

            recentlyProject.add(project);
        }

        return recentlyProject;
    }

    // 진행 중인 프로젝트
    private List<ProgressProject> searchProgressProject(List<MemberProject> memberProject, List<Project> projects, int projectNum) {

        projects = new ArrayList<>();


        for(int i = 0; i<memberProject.size(); i++) {
            Project project = projectRepository.findById(memberProject.get(i).getProject().getProject_id()).get();
            // Status 가 ING 인 것만
            if(project.getProject_status().equals(Status.ING)) {
                projects.add(project);
            }
        }

        // 마감날짜 순으로 - 마감 날짜를 기준으로 오름차순
        Collections.sort(projects, new SortByEndDate());

        if(projectNum > projects.size()) {
            projectNum = projects.size();
        }

        List<ProgressProject> progressProjects = new ArrayList<>();

        for(int i = 0; i<projectNum; i++) {
            ProgressProject project = ProgressProject.builder()
                    .projectId(projects.get(i).getProject_id())
                    .projectName(projects.get(i).getProject_name())
                    .projectStartedDate(projects.get(i).getStart_date())
                    .projectStatus(projects.get(i).getProject_status()).build();

            progressProjects.add(project);
        }

        return progressProjects;
    }

    // 포트폴리오
    private List<Portfolio> searchPortPolio(List<MemberProject> memberProject, List<Project> projects, int projectNum) {
        projects = new ArrayList<>();

        for(int i = 0; i<memberProject.size(); i++) {
            Project project = projectRepository.findById(memberProject.get(i).getProject().getProject_id()).get();
            // Status 가 END 인 것만
            if(project.getProject_status().equals(Status.END)) {
                projects.add(project);
            }
        }

        // 가장 최근에 끝낸 순으로 - 마감 날짜 기준 내림차순
        Collections.sort(projects, new SortByEndDate().reversed());

        if(projectNum > projects.size()) {
            projectNum = projects.size();
        }

        List<Portfolio> portfolios = new ArrayList<>();
        for(int i = 0; i<projectNum; i++) {
            Portfolio portfolio = Portfolio.builder()
                    .projectId(projects.get(i).getProject_id())
                    .projectName(projects.get(i).getProject_name())
                    .projectStartDate(projects.get(i).getStart_date())
                    .projectEndDate(projects.get(i).getEnd_date())
                    .projectImage(projects.get(i).getProject_image())
                    .projectStatus(projects.get(i).getProject_status()).build();

            portfolios.add(portfolio);
        }

        return portfolios;
    }

    // 테스트용 Member_Project 저장 코드
    public void saveMemberProject(Long member_id, Long project_id, Long schedule_id) {
        Member member = memberRepository.findById(member_id).get();
        Project project = projectRepository.findById(project_id).get();

        MemberProject memberProject = MemberProject.builder()
                .member(member)
                .project(project).build();



        memberProjectRepository.save(memberProject);
    }

    // Member 로 MemberProject 조회
    private List<MemberProject> findMemberProject(Member member) {
        List<MemberProject> memberProject = memberProjectRepository.findByMember(member);

        return memberProject;
    }

    // 프로젝트 정렬 : 시작 일자 기준으로 정렬
    static class SortByStartDate implements Comparator<Project> {
        @Override
        public int compare(Project a, Project b) {
            return a.getStart_date().compareTo(b.getStart_date());
        }
    }

    // 프로젝트 정렬 끝난 일자 기준으로 정렬
    static class SortByEndDate implements Comparator<Project> {
        @Override
        public int compare(Project a, Project b) {
            return a.getEnd_date().compareTo(b.getEnd_date());
        }
    }
}


