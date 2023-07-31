package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Domain.Dto.MemberLogoutRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberSignUpEmailDuplicationRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberVerificationEmailRequestDto;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity join(MemberRequestDto memberRequestDto);

    ResponseEntity validateDuplicateMember(MemberSignUpEmailDuplicationRequestDto memberSignUpEmailDuplicationRequestDto) throws Exception;

    ResponseEntity verificationEmail(MemberVerificationEmailRequestDto memberVerificationEmailRequestDto);

    JwtToken login(String email, String password);

    JwtToken logout(MemberLogoutRequestDto memberLogoutRequestDto);
}