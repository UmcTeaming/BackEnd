package com.teaming.TeamingServer.Config.Jwt;

import com.teaming.TeamingServer.Domain.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {
    JwtToken generateToken(Member member);
    Authentication getAuthentication(String accessToken);
    boolean validateToken(String token);
    void checkMemberId(Authentication authentication, HttpServletRequest request);
}
