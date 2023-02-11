package com.example.guess_music;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.exceptionHandling().authenticationEntryPoint((req, rsp, e) -> {rsp.sendError(401); System.out.println("oh1");})
                .accessDeniedHandler((req, rsp, e) -> {rsp.sendError(403); System.out.println("oh2");})
                .and().authorizeRequests().requestMatchers("/Game/**").authenticated()
                .requestMatchers("/manage/**").hasRole("MANAGER")
                .anyRequest().permitAll()
                .and().formLogin()
                .loginPage("/auth/loginForm").loginProcessingUrl("/login").failureUrl("/auth/loginForm").permitAll().defaultSuccessUrl("/")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/")
                .and().csrf().disable();

        return http.build();
    }

}
