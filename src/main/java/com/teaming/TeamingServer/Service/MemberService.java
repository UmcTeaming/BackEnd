package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberSignUpEmailDuplicationRequestDto;
import com.teaming.TeamingServer.Domain.Dto.MemberVerificationEmailRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity join(MemberRequestDto memberRequestDto);

    ResponseEntity validateDuplicateMember(MemberSignUpEmailDuplicationRequestDto memberSignUpEmailDuplicationRequestDto) throws Exception;

    ResponseEntity verificationEmail(MemberVerificationEmailRequestDto memberVerificationEmailRequestDto);
}
