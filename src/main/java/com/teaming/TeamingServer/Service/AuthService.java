package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberResetPasswordRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity join(MemberRequestDto memberRequestDto);

    ResponseEntity validateEmailRequest(String email) throws Exception;

    ResponseEntity verificationEmail(String inputCode);

    ResponseEntity login(String email, String password);

    ResponseEntity resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto) throws Exception;
}
