package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberResetPasswordRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberSignUpEmailDuplicationRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberVerificationEmailRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity join(MemberRequestDto memberRequestDto);

    ResponseEntity validateDuplicateMember(MemberSignUpEmailDuplicationRequestDto memberSignUpEmailDuplicationRequestDto) throws Exception;

    ResponseEntity verificationEmail(String inputCode);

    ResponseEntity login(String email, String password);

    ResponseEntity resetPassword(MemberResetPasswordRequestDto memberResetPasswordRequestDto) throws Exception;
}
