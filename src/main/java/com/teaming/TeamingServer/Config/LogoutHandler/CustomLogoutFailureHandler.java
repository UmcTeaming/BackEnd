package com.teaming.TeamingServer.Config.LogoutHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;


import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class CustomLogoutFailureHandler implements LogoutSuccessHandler {

//    public void onLogoutFailure(HttpServletRequest request, HttpServletResponse response, Authentication authentication, Exception exception) throws IOException, ServletException {
//
//    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 로그아웃 실패 시 처리할 로직을 구현합니다.
        // 예: 로그아웃 실패 메시지를 설정하고 실패 상태 코드를 반환한다던지, 로깅 등의 동작을 수행할 수 있습니다.

        // 예시: 로그아웃 실패 시 500 Internal Server Error 상태 코드와 메시지를 반환
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("로그아웃 실패");

    }
}
