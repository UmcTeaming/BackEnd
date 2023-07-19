package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;

public interface MemberService {
    Integer join(MemberRequestDto memberRequestDto);
}
