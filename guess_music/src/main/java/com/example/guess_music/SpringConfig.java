package com.example.guess_music;

import com.example.guess_music.repository.JpaMemberRepository;
import com.example.guess_music.repository.MemberRepository;
import com.example.guess_music.service.MemberService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Autowired
    public SpringConfig(EntityManager em) {
        this.em = em;
    }

    private EntityManager em;

    @Bean
    public MemberService memberService(){return new MemberService(memberRepository());}
    @Bean
    public MemberRepository memberRepository(){
        return new JpaMemberRepository(em);
    }

}
