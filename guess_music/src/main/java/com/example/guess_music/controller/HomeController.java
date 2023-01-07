package com.example.guess_music.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(HttpServletRequest request) {
        //일단 로그인 여부 상관없이 home에 오면 seq값을 1로 초기화 시킨다.
        HttpSession session=request.getSession();
        session.setAttribute("seq",1);
        return "home";
    }
}
