package com.teaming.TeamingServer.Controller;


import com.teaming.TeamingServer.Domain.Dto.*;
import com.teaming.TeamingServer.Exception.BadRequestException;
import com.teaming.TeamingServer.Service.AuthService;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController // 해당 클래스가 컨트롤러임을 알리고 bean으로 등록하기 위함 - ResponseBody 어노테이션도 포함하고 있음
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/auth/signup")
    public BaseResponse signup(@RequestBody MemberRequestDto memberRequestDto) {
        authService.join(memberRequestDto);
        return new BaseResponse("회원가입이 완료되었습니다.");
    }

    // 이메일 중복체크
    @PostMapping("/auth/email-duplication")
    public BaseResponse duplicateEmail(@RequestBody MemberSignUpEmailDuplicationRequestDto memberSignUpEmailDuplicationRequestDto) throws Exception {
        authService.validateEmailRequest(memberSignUpEmailDuplicationRequestDto.getEmail());
        return new BaseResponse("사용 가능한 이메일입니다.");
    }

    // 이메일 인증
    @PostMapping("/auth/email-verification")
    public BaseResponse verifyEmailCode(@RequestBody MemberVerificationEmailRequestDto memberVerificationEmailRequestDto) {
        authService.verifyEmailCode(memberVerificationEmailRequestDto.getAuthentication());
        return new BaseResponse("사용자 이메일 인증 성공");
    }

    // 로그인
    @PostMapping("/auth/login")
    public BaseResponse login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        return new BaseResponse("로그인 성공", authService.login(memberLoginRequestDto.getEmail(), memberLoginRequestDto.getPassword()));
    }

    // 비밀번호 재설정
    @PatchMapping("/auth/reset-password")
    public BaseResponse resetPassword(@RequestBody MemberResetPasswordRequestDto memberResetPasswordRequestDto) {
        authService.resetPassword(memberResetPasswordRequestDto);
        return new BaseResponse("비밀번호 재설정 메일이 발송되었습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    BaseErrorResponse handleBadRequestException(Exception ex) {
        return new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}