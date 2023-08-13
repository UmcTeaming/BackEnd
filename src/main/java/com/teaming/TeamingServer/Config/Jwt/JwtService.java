package com.teaming.TeamingServer.Config.Jwt;

import com.teaming.TeamingServer.Domain.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@Service
public class JwtService {
    private final JwtTokenProvider jwtTokenProvider;

    public boolean VerifyAccess(String accessToken, Member member) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        String email = member.getEmail();

        return authentication.getName().equals(email);
    }
}
