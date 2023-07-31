package com.teaming.TeamingServer.Config.LogoutHandler;

import com.teaming.TeamingServer.Controller.MemberController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Handle logout success here.
        // You can set the appropriate response status code and message.

        // Example: Set a 200 OK status code and a custom success message.
        response.setStatus(HttpServletResponse.SC_OK);

        response.getWriter().write("logout success");
    }
}
