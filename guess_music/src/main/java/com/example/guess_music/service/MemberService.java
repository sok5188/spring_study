package com.example.guess_music.service;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.MemberDetail;
import com.example.guess_music.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Slf4j
@Transactional
@Service
public class MemberService implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository;

    public String join(Member member){

        try {
            validateMember(member);
        }catch (IllegalStateException e){
            log.warn(e.getMessage());
            return "FAIL";
        }
        //member.setPassword(getEncodedPassword(member.getPassword()));
        Member save = memberRepository.save(member);
        return save.getUsername();
    }
    private void validateMember(Member member){
        memberRepository.findByUsername(member.getUsername()).ifPresent(m->{
            throw new IllegalStateException("already exist id");});
    }
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(String id){ return memberRepository.findByUsername(id); }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> byUsername = memberRepository.findByUsername(username);
        if(byUsername.isPresent()){
            return new MemberDetail(byUsername.get());
        }
        throw new UsernameNotFoundException("cant find : "+username);
    }

    public void updateName(String name,String username){
        memberRepository.updateName(name,username);
    }

}
