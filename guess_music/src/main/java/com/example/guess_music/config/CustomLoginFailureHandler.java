package com.example.guess_music.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errormsg = null;
        System.out.println("custom login failure handler called");
        if (exception instanceof UsernameNotFoundException) {
            //이 오류는 감춰지고 BadCredential로 간주 됨
            //노션에 적은 것 참고
            errormsg = "1";
        }else if(exception instanceof BadCredentialsException){
            errormsg="2";
        }
        else if (exception instanceof InternalAuthenticationServiceException) {
            errormsg = "3";
        } else if (exception instanceof DisabledException) {
            errormsg ="4";
        } else if (exception instanceof CredentialsExpiredException) {
            errormsg ="5";
        }else if(exception instanceof SessionAuthenticationException){
            errormsg="6";
        }
        setDefaultFailureUrl("/auth/loginForm?error=true&exception="+errormsg);

        super.onAuthenticationFailure(request,response,exception);
    }
}
