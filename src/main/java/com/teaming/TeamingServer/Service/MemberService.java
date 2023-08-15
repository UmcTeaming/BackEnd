package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.CheckCurrentPasswordRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberChangePasswordRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberChangeProfileImageRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberNicknameChangeRequestDto;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity changePassword(Long memberId, MemberChangePasswordRequestDto memberChangePasswordRequestDto); // PasswordChange Request 매개변수로 들어갈 예정

    ResponseEntity checkCurrentPassword(Long memberId, CheckCurrentPasswordRequestDto checkCurrentPasswordRequestDto); // 비밀번호 변경을 위한 현재 비밀번호 체크 : CheckCurrentPasswordRequest 가 매개변수로 들어갈 예정

    ResponseEntity MemberMyPage(Long memberId);

    ResponseEntity changeNickName(Long memberId, MemberNicknameChangeRequestDto memberNicknameChangeRequestDto); // 닉네임 바꾸기

    ResponseEntity changeProfileImage(Long memberId, MemberChangeProfileImageRequestDto memberChangeProfileImageRequestDto);

    ResponseEntity mainPage(Long memberId);

    ResponseEntity portfolioPage(Long MemberId);

    void saveMemberProject(Long member_id, Long project_id, Long schedule_id); // 테스트용

}
