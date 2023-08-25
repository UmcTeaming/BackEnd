package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Domain.Dto.*;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    void join(MemberRequestDto memberRequestDto);

    void validateEmailRequest(String email) throws Exception;

    void verifyEmailCode(String inputCode);

    MemberLoginResponse login(String email, String password);

    void resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto);
}
