package com.teaming.TeamingServer.Controller;

import com.teaming.TeamingServer.Domain.Dto.CheckCurrentPasswordRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberChangePasswordDto;
import com.teaming.TeamingServer.Domain.Dto.MemberNicknameChangeRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberSignUpEmailDuplicationRequestDto;
import com.teaming.TeamingServer.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                        , @RequestBody MemberChangePasswordDto memberChangePasswordDto) {

        return memberService.changePassword(memberId, memberChangePasswordDto);
    }

    @GetMapping("/member/{memberId}/mypage")
    public ResponseEntity myPage(@PathVariable("memberId") Long memberId) {
        return memberService.MemberMyPage(memberId);
    }

    @PatchMapping("/member/{memberId}/mypage/change-nickname")
    public ResponseEntity changeNickName(@PathVariable("memberId") Long memberId
                                         , @RequestBody MemberNicknameChangeRequestDto memberNicknameChangeRequestDto) {
        return memberService.changeNickName(memberId, memberNicknameChangeRequestDto);
    }

}
