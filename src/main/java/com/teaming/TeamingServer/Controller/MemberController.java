package com.teaming.TeamingServer.controller;


import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberSignUpEmailDuplicationRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberVerificationEmailRequestDto;
import com.teaming.TeamingServer.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller            // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/member/signup")
    @ResponseBody // json 으로 반환해주는 어노테이션
    public ResponseEntity signup(@RequestBody MemberRequestDto memberRequestDto) {

        // 회원가입
        return memberService.join(memberRequestDto);

    }

    // 이메일 중복체크
    @PostMapping("/member/email-duplication")
    @ResponseBody // json 으로 반환해주는 어노테이션
    public ResponseEntity duplicateEmail(@RequestBody MemberSignUpEmailDuplicationRequestDto memberSignUpEmailDuplicationRequestDto) throws Exception {

        return memberService.validateDuplicateMember(memberSignUpEmailDuplicationRequestDto);

    }

    // 이메일 인증
    @PostMapping("/member/email-verification")
    @ResponseBody // json 으로 반환해주는 어노테이션
    public ResponseEntity verificationEmail(@RequestBody MemberVerificationEmailRequestDto memberVerificationEmailRequestDto) {

        return memberService.verificationEmail(memberVerificationEmailRequestDto);
    }

}