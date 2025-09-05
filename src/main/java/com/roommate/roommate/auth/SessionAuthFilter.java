package com.roommate.roommate.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class SessionAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        
        // 인증X - 정적 파일들도 포함
        if (requestURI.startsWith("/auth/") || 
            requestURI.startsWith("/swagger-ui/") || 
            requestURI.startsWith("/swagger-ui.html") ||
            requestURI.startsWith("/v3/api-docs") ||
            requestURI.startsWith("/swagger-resources/") ||
            requestURI.startsWith("/webjars/") ||
            requestURI.startsWith("/api/test/") ||
            requestURI.startsWith("/health")||
            requestURI.equals("/") ||
            requestURI.endsWith(".html") ||
            requestURI.endsWith(".css") ||
            requestURI.endsWith(".js") ||
            requestURI.endsWith(".ico") ||
            requestURI.endsWith(".png") ||
            requestURI.endsWith(".jpg") ||
            requestURI.endsWith(".jpeg") ||
            requestURI.endsWith(".gif") ||
            requestURI.endsWith(".svg")) {

            chain.doFilter(request, response);
            return;
        }
        
        // 세션에서 사용자 정보 확인
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // userId를 request attribute로 설정
        Long userId = (Long) session.getAttribute("userId");
        httpRequest.setAttribute("userId", userId);
        
        chain.doFilter(request, response);
    }
}
