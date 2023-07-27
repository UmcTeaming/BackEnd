package com.teaming.TeamingServer.Config.Jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teaming.TeamingServer.Config.auth.PrincipalDetails;
import com.teaming.TeamingServer.Domain.Dto.MemberLoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.auth0.jwt.JWT;


import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties = new JwtProperties();

    // Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
    // 인증 요청시에 실행되는 함수 => /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 진입");

        // request 에 있는 username 과 password 를 파싱해서 자바 Object 로 받기
        ObjectMapper objectMapper = new ObjectMapper();

        MemberLoginRequestDto memberLoginRequestDto = null;

        try {
            memberLoginRequestDto = objectMapper.readValue(request.getInputStream(), MemberLoginRequestDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("JwtAuthenticationFilter : " + memberLoginRequestDto);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        memberLoginRequestDto.getEmail(),
                        memberLoginRequestDto.getPassword());

        System.out.println("JwtAuthenticationFilter : 토큰 생성 완료");

        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        System.out.println("Authentication : " + principalDetails.getMember());

        return authentication;
    }

    // JWT Token 생성해서 response 에 담아주기
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response
            , FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+jwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetails.getMember().getMember_id())
                .withClaim("username", principalDetails.getMember().getName())
                .sign(Algorithm.HMAC512(jwtProperties.secret));

        response.addHeader(jwtProperties.HEADER_STRING, jwtProperties.TOKEN_PREFIX+jwtToken);
    }
}
