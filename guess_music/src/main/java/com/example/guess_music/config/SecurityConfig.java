package com.example.guess_music.config;

import com.example.guess_music.service.CustomOAuth2UserService;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    private CustomLoginFailureHandler customLoginFailureHandler;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring().requestMatchers("/css/**","/js/**");
        };
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration source = new CorsConfiguration();
        source.setAllowCredentials(false); //쿠키 받을 것 인지 설정
        source.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://localhost:8080"));
        source.setAllowedMethods(Arrays.asList("GET"));
        source.addAllowedMethod("POST");
        source.addAllowedMethod("PUT");
        source.addAllowedMethod("DELETE");
        source.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource url= new UrlBasedCorsConfigurationSource();
        url.registerCorsConfiguration("/**", source);

        return url;
    }

    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.addFilterBefore(new CustomLoginPageFilter(), DefaultLoginPageGeneratingFilter.class);
        http.exceptionHandling().authenticationEntryPoint((req, rsp, e) -> { System.out.println("authenticationEntryPoint Error:"+req.getAuthType()); rsp.sendRedirect("/auth/loginForm");})
                .accessDeniedHandler((req, rsp, e) -> {  System.out.println("accessDenied Error"); rsp.sendRedirect("/auth/accessDenied");
                })
                .and().sessionManagement().maximumSessions(1).expiredUrl("/auth/loginForm").maxSessionsPreventsLogin(true)
                .and()
                .and().authorizeRequests().requestMatchers("/Game/**").authenticated()
                .requestMatchers("/manage/**").access("hasRole('MANAGER') or hasRole('ADMIN')")
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .loginPage("/auth/loginForm").loginProcessingUrl("/login").failureUrl("/auth/loginForm").permitAll().defaultSuccessUrl("/").successHandler((req, res, auth) -> {req.getSession().setAttribute("username",auth.getName());
                    res.sendRedirect("/");}).failureHandler(customLoginFailureHandler)
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true).addLogoutHandler((request, response, auth) -> {
                    try {
                        request.logout();
                    } catch (ServletException e) {
                        System.out.println(e.getMessage());
                    }
                })
                .and().oauth2Login().loginPage("/auth/loginForm").defaultSuccessUrl("/").successHandler((req, res, auth) -> {req.getSession().setAttribute("username",auth.getName()); res.sendRedirect("/auth/oAuthUserCheck");}).userInfoEndpoint().userService(customOAuth2UserService)
                .and().and().csrf().disable()
        ;

        return http.build();
    }

}
