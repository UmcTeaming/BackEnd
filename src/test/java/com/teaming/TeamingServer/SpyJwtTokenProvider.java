package com.teaming.TeamingServer;

import com.teaming.TeamingServer.Config.Jwt.JwtToken;
import com.teaming.TeamingServer.Config.Jwt.JwtTokenProvider;
import com.teaming.TeamingServer.Domain.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public class SpyJwtTokenProvider implements JwtTokenProvider {
    @Override
    public JwtToken generateToken(Authentication authentication) {
        return null;
    }

    @Override
    public JwtToken generateToken(Member member) {
        return null;
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public void checkMemberId(Authentication authentication, HttpServletRequest request) {

    }
}
