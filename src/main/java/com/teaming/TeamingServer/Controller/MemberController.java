package com.teaming.TeamingServer.Controller;

import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Domain.Dto.mainPageDto.TestDto;
import com.teaming.TeamingServer.Service.MemberService;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함 - ResponseBody 어노테이션도 포함하고 있음
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/{memberId}/change-password/check-password")
    public ResponseEntity checkCurrentPassword(@PathVariable("memberId") Long memberId
                                            , @RequestBody CheckCurrentPasswordRequestDto checkCurrentPasswordRequestDto) {

        return memberService.checkCurrentPassword(memberId, checkCurrentPasswordRequestDto);

    }

    @PostMapping("/member/{memberId}/change-password")
    public ResponseEntity changePassword(@PathVariable("memberId") Long memberId
                                        , @RequestBody MemberChangePasswordRequestDto memberChangePasswordRequestDto) {

        return memberService.changePassword(memberId, memberChangePasswordRequestDto);
    }

    @GetMapping("/member/{memberId}/mypage")
    public ResponseEntity myPage(@PathVariable("memberId") Long memberId, @RequestHeader("Authorization") String accessToken) {

        ResponseEntity response = null;

        log.info("accessToken : " + accessToken);

        try {
            if(StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer")) {
                accessToken = accessToken.substring(7);
            }

            response = memberService.MemberMyPage(memberId, accessToken);
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), illegalArgumentException.getMessage()));
        }

        return response;
    }

    @PatchMapping("/member/{memberId}/mypage/change-nickname")
    public ResponseEntity changeNickName(@PathVariable("memberId") Long memberId
                                         , @RequestBody MemberNicknameChangeRequestDto memberNicknameChangeRequestDto) {
        return memberService.changeNickName(memberId, memberNicknameChangeRequestDto);
    }

    @PatchMapping("/member/{memberId}/mypage/change-image")
    public ResponseEntity changeProfileImage(@PathVariable("memberId") Long memberId
                                             , @RequestBody MemberChangeProfileImageRequestDto memberChangeProfileImageRequestDto) {
        return memberService.changeProfileImage(memberId, memberChangeProfileImageRequestDto);
    }

    @GetMapping("/member/{memberId}/home")
    public ResponseEntity mainPage(@PathVariable("memberId") Long memberId) {
        return memberService.mainPage(memberId);
    }

    @GetMapping("/member/{memberId}/portfolio")
    public ResponseEntity portfolioPage(@PathVariable("memberId") Long memberId) {
        return memberService.portfolioPage(memberId);
    }

    @PostMapping("/member/saveData")
    public ResponseEntity saveData(@RequestBody TestDto testDto) {
        memberService.saveMemberProject(testDto.getMember_id(), testDto.getProject_id(), testDto.getSchedule_id());
        return ResponseEntity.ok("성공");
    }
}
