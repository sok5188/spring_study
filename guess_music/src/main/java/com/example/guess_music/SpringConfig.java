package com.example.guess_music;

import com.example.guess_music.repository.GameRepository;
import com.example.guess_music.repository.JpaGameRepository;
import com.example.guess_music.repository.JpaMemberRepository;
import com.example.guess_music.repository.MemberRepository;
import com.example.guess_music.service.GameService;
import com.example.guess_music.service.MemberService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Autowired
    public SpringConfig(EntityManager em,EntityManager em2) {
        this.em = em;
        this.em2=em2;
    }

    private EntityManager em,em2;

    @Bean
    public MemberService memberService(){return new MemberService(memberRepository());}
    @Bean
    public MemberRepository memberRepository(){
        return new JpaMemberRepository(em);
    }

    @Bean
    public GameService gameService() {
        return new GameService(gameRepository());
    }

    @Bean
    public GameRepository gameRepository() {
        return new JpaGameRepository(em2);
    }
}
