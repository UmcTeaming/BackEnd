package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MemberLoginResponse;
import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberResetPasswordRequestDto;

public interface AuthService {
    void join(MemberRequestDto memberRequestDto);

    void validateEmailRequest(String email) throws Exception;

    void verifyEmailCode(String inputCode);

    MemberLoginResponse login(String email, String password);

    void resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto);
}
