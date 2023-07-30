package com.teaming.TeamingServer.Config.Jwt;

import com.teaming.TeamingServer.Config.Redis.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 JWT 를 받아옵니다.
        String token = resolveToken((HttpServletRequest) request);

        // log를 이용한 확인
        log.info("[This is Verifying token!!]"); // 그냥 스트링 출력해주는 로
        log.info(((HttpServletRequest) request).getRequestURL().toString());

        // 토큰 유효성 검사
        if(token != null && jwtTokenProvider.validateToken(token)) {

            // Redis에 해당 accessToken logout 여부를 확인
            String isLogout = (String) redisUtil.get(token);

            // 로그아웃이 없는(되어 있지 않은) 경우 해당 토큰은 정상적으로 작동하기
            if (ObjectUtils.isEmpty(isLogout)) {
                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                // SecurityContext 에 Authentication 객체를 저장합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }
        chain.doFilter(request, response);
    }

    // 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}