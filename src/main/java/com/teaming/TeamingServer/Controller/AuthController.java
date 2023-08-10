package com.teaming.TeamingServer.Controller;


import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Service.AuthService;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함 - ResponseBody 어노테이션도 포함하고 있음
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/auth/signup")
    public ResponseEntity signup(@RequestBody MemberRequestDto memberRequestDto) {

        // 회원가입
        return authService.join(memberRequestDto);

    }

    // 이메일 중복체크
    @PostMapping("/auth/email-duplication")
    public ResponseEntity duplicateEmail(@RequestBody MemberSignUpEmailDuplicationRequestDto memberSignUpEmailDuplicationRequestDto) throws Exception {
        return authService.validateDuplicateMember(memberSignUpEmailDuplicationRequestDto);
    }

    // 이메일 인증
    @PostMapping("/auth/email-verification")
    public ResponseEntity verificationEmail(@RequestBody MemberVerificationEmailRequestDto memberVerificationEmailRequestDto) {
        return authService.verificationEmail(memberVerificationEmailRequestDto);
    }

    // 로그인
    @PostMapping("/auth/login")
    public ResponseEntity login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {

        JwtToken token;

        try {
            token = authService.login(memberLoginRequestDto.getEmail(), memberLoginRequestDto.getPassword());
        }
        catch (IllegalArgumentException | AuthenticationException illegalArgumentException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseErrorResponse(HttpStatus.FORBIDDEN.value(), "잘못된 email 혹은 password 입니다."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<JwtToken>(HttpStatus.OK.value(), "로그인 성공", token));
    }

    // 비밀번호 재설정
    @PatchMapping("/auth/reset-password")
    public ResponseEntity resetPassword(@RequestBody MemberResetPasswordRequestDto memberResetPasswordRequestDto) throws Exception {
        return authService.resetPassword(memberResetPasswordRequestDto);
    }


}