package com.example.guess_music.controller;

import com.example.guess_music.domain.Member;
import com.example.guess_music.service.MemberService;
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
    public String login(MemberForm form){
        String id = form.getId();
        String password = form.getPassword();
        Optional<Member> optional= memberService.findOne(id);
        Member member=optional.get();
        if(password.equals(member.getPassword())) return "redirect:/";
        else {
            //need to alert
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
