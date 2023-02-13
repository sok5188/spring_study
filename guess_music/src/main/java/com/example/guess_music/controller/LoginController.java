package com.example.guess_music.controller;

import com.example.guess_music.domain.Member;
import com.example.guess_music.domain.Role;
import com.example.guess_music.domain.SignInForm;
import com.example.guess_music.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/auth")
public class LoginController {
    private final MemberService memberService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/loginForm")
    public String createLoginForm(){
        System.out.println("login called");
        return "login/createLoginForm"; }

    @GetMapping("/joinForm")
    public String createSignInForm() {
        return "login/createSignInForm";
    }
    @PostMapping("/signIn")
    public String signIn(SignInForm form){

        Member user = new Member();
        user.setUsername(form.getUsername());
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        String encodePwd = bCryptPasswordEncoder.encode(form.getPassword());
        user.setPassword(encodePwd);
        user.setRole(Role.USER);
        System.out.println("user info :"+user.getUsername()+"  / "+user.getName()+"/ "+user.getEmail());

        memberService.join(user);

        return "redirect:/auth/loginForm";
    }

    @GetMapping("/checkLogin")
    @ResponseBody
    public String checkLogin(HttpServletRequest request){
        return memberService.checkLogin(request);
    }
    @GetMapping("/accessDenied")
    public String accessDeny(){
        return "login/accessDenied";
    }
    @GetMapping("/findId")
    public String findId(){
        //나중에 인증기능 추가하고 구현
        return "Not Implemented";
    }
    @GetMapping("/findPwd")
    public String findPwd(){
        //나중에 인증기능 추가하고 구현
        return "Not Implemented";
    }
}
