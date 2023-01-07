package com.example.guess_music.controller;

import com.example.guess_music.domain.Member;
import com.example.guess_music.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class LoginController {
    private final MemberService memberService;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String createLoginForm(){ return "/login/createLoginForm"; }

    @PostMapping("/login")
    public String login(MemberForm form, HttpServletRequest request){
        HttpSession session=request.getSession();
        String id = form.getId();
        String password = form.getPassword();
        Optional<Member> optional= memberService.findOne(id);
        Member member=optional.get();
        if(password.equals(member.getPassword())){
            session.setAttribute("login",true);
            return "redirect:/";}
        else {
            //need to alert
            //front에서 뭐 click listner같은 애들로 확인 여부체크하고 여기서는 redirect하지 않는 방향?? (나중에 체크)
            return "redirect:/login";}
    }

    @GetMapping("/signIn")
    public String createSignInForm() {
        return "/login/createSignInForm";
    }
    @PostMapping("/signIn")
    public String signIn(SignInForm form){
        Member member = new Member();
        member.setId(form.getId());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        memberService.join(member);

        return "redirect:/";
    }

}
