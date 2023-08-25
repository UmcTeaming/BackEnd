package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.request.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.request.MemberResetPasswordRequestDto;
import com.teaming.TeamingServer.Domain.Dto.response.MemberLoginResponse;

public interface AuthService {
    void join(MemberRequestDto memberRequestDto);

    void validateEmailRequest(String email) throws Exception;

    void verifyEmailCode(String inputCode);

    MemberLoginResponse login(String email, String password);

    void resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto);
}
