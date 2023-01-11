package com.example.guess_music.service;

import com.example.guess_music.domain.Member;
import com.example.guess_music.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class MemberService {
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    private final MemberRepository memberRepository;

    public String join(Member member){

        validateMember(member);
        memberRepository.save(member);
        return member.getId();
    }
    private void validateMember(Member member){
        memberRepository.findById(member.getId()).ifPresent(m->{
            throw new IllegalStateException("already exist id");});
    }
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(String id){ return memberRepository.findById(id); }

    public String checkLogin(HttpServletRequest request) {
        HttpSession session=request.getSession();
        if(session.getAttribute("login")==null){
            return "False";
        }
        return session.getAttribute("login").toString();
    }


}
