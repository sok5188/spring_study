package com.example.guess_music.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class CustomLoginPageFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && ((HttpServletRequest)request).getRequestURI().equals("/auth/loginForm")) {
            System.out.println("user is authenticated but trying to access login page, redirecting to /");
            ((HttpServletResponse)response).sendRedirect("/");
        }
        chain.doFilter(request, response);
    }
}
