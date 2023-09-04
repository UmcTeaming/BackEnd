package com.teaming.TeamingServer.Config.Jwt;

import com.teaming.TeamingServer.Exception.BaseException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@AllArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProviderImpl jwtTokenProviderImpl;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, BaseException {
        String token = jwtTokenProviderImpl.resolveToken((HttpServletRequest) request);

        try {
            // 토큰 유효성 검사
            if(token != null && jwtTokenProviderImpl.validateToken(token)) {
//                // (추가) Redis 에 해당 accessToken logout 여부 확인
//                String isLogout = (String) redisTemplate.opsForValue().get(token);
//
//                if(isLogout != null) {
//                    throw new BaseException(HttpStatus.FORBIDDEN.value(), "유효하지 않은 AccessToken 입니다.");
//                }

                Authentication authentication = jwtTokenProviderImpl.getAuthentication(token);
                jwtTokenProviderImpl.checkMemberId(authentication, (HttpServletRequest) request);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (BaseException baseException) {
            // 예외 발생 시 적절한 응답 생성 및 전송
            HttpServletResponse result = (HttpServletResponse) response;
            result.setStatus(baseException.getCode());
            result.setContentType("application/json;charset=UTF-8");
            result.getWriter().write("{\"status\":\"" + baseException.getCode() + "\",\"message\":\"" + baseException.getMessage() + "\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}