package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberResetPasswordRequestDto;
import com.teaming.TeamingServer.common.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    BaseResponse join(MemberRequestDto memberRequestDto);

    ResponseEntity validateEmailRequest(String email) throws Exception;

    ResponseEntity verificationEmail(String inputCode);

    ResponseEntity login(String email, String password);

    ResponseEntity resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto) throws Exception;
}
