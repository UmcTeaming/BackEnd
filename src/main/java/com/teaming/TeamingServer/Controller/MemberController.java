package com.teaming.TeamingServer.Controller;

import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.TestDto;
import com.teaming.TeamingServer.Service.MemberService;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함 - ResponseBody 어노테이션도 포함하고 있음
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/member/{memberId}/change-password/check-password")
    public ResponseEntity checkCurrentPassword(@PathVariable("memberId") Long memberId
                                            , @RequestBody CheckCurrentPasswordRequestDto checkCurrentPasswordRequestDto) {

        return memberService.checkCurrentPassword(memberId, checkCurrentPasswordRequestDto);

    }

    @PostMapping("/api/member/{memberId}/change-password")
    public ResponseEntity changePassword(@PathVariable("memberId") Long memberId
                                        , @RequestBody MemberChangePasswordRequestDto memberChangePasswordRequestDto) {

        return memberService.changePassword(memberId, memberChangePasswordRequestDto);
    }

    @GetMapping("/api/member/{memberId}/mypage")
    public ResponseEntity myPage(@PathVariable("memberId") Long memberId) {
        return memberService.MemberMyPage(memberId);
    }

    @PatchMapping("/api/member/{memberId}/mypage/change-nickname")
    public ResponseEntity changeNickName(@PathVariable("memberId") Long memberId
                                         , @RequestBody MemberNicknameChangeRequestDto memberNicknameChangeRequestDto) {
        return memberService.changeNickName(memberId, memberNicknameChangeRequestDto);
    }

    @PatchMapping("/api/member/{memberId}/mypage/change-image")
    public ResponseEntity changeProfileImage(@PathVariable("memberId") Long memberId
                                             , @RequestBody MemberChangeProfileImageRequestDto memberChangeProfileImageRequestDto) {
        return memberService.changeProfileImage(memberId, memberChangeProfileImageRequestDto);
    }

    @GetMapping("/api/member/{memberId}/home")
    public ResponseEntity mainPage(@PathVariable("memberId") Long memberId) {
        return memberService.mainPage(memberId);
    }

    @GetMapping("/api/member/{memberId}/portfolio")
    public ResponseEntity portfolioPage(@PathVariable("memberId") Long memberId) {
        return memberService.portfolioPage(memberId);
    }

    @PostMapping("/api/member/saveData")
    public ResponseEntity saveData(@RequestBody TestDto testDto) {
        memberService.saveMemberProject(testDto.getMember_id(), testDto.getProject_id(), testDto.getSchedule_id());
        return ResponseEntity.ok("성공");
    }
}
