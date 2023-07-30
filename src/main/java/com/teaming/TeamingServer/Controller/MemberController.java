package com.teaming.TeamingServer.Controller;


import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Domain.Dto.MemberLoginRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberSignUpEmailDuplicationRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberVerificationEmailRequestDto;
import com.teaming.TeamingServer.Service.MemberService;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함
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

    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {

        JwtToken token = memberService.login(memberLoginRequestDto.getEmail(), memberLoginRequestDto.getPassword());

        if(token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseErrorResponse(HttpStatus.FORBIDDEN.value(), "잘못된 email 혹은 password 입니다."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<JwtToken>(HttpStatus.OK.value(), "로그인 성공", token));
    }

}