package com.example.guess_music.controller;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.OAuthSingUpForm;
import com.example.guess_music.domain.auth.Role;
import com.example.guess_music.domain.auth.SignInForm;
import com.example.guess_music.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


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
        if(user.getUsername().equals("admin"))
            user.setRole(Role.ROLE_ADMIN);
        else if(user.getUsername().equals("manager"))
            user.setRole(Role.ROLE_MANAGER);
        else
            user.setRole(Role.ROLE_USER);

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
    @GetMapping("/invalidSession")
    public String invalidSession(){
        System.out.println("Invalid Session Found");
        return "redirect:/auth/loginForm";
    }
    @GetMapping("/oAuthUserCheck")
    public String oAuthUsercheck(HttpSession session){
        String username = (String) session.getAttribute("name");
        Optional<Member> opt = memberService.findOne(username);
        //해당 유저가 존재할 때 해당 유저의 이름이 설정되어 있지 않으면 설정 페이지로 이동
        //이미 존재한다면 홈으로 이동
        if(opt.isPresent()){
            if(opt.get().getName()==null)
                return "login/oAuthSignUp";
            else return "redirect:/";
        }else
            return "OAuth User Check Error..";

    }
    @PostMapping("/oAuthSignUp")
    public String oAuthSignUp(OAuthSingUpForm form,HttpSession session){
        String username = (String) session.getAttribute("name");
        Optional<Member> opt = memberService.findOne(username);
        if(opt.isPresent()){
            //입력한 이름으로 해당 유저 설정 후 홈으로 이동
            memberService.updateName(form.getName(),username);
            return "redirect:/";
        }else
            return "OAuth User Check Error..";

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
