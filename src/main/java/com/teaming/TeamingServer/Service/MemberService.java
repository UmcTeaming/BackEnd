package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.Dto.MemberRequestDto;
import com.teaming.TeamingServer.Domain.entity.Member;

public interface MemberService {
    Integer join(Member member);

    boolean validateDuplicateMember(String email);
}
