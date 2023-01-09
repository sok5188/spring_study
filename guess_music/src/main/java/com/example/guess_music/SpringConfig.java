package com.example.guess_music;

import com.example.guess_music.repository.*;
import com.example.guess_music.service.GameService;
import com.example.guess_music.service.ManagerService;
import com.example.guess_music.service.MemberService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Autowired
    public SpringConfig(EntityManager em, EntityManager em2, EntityManager em3) {
        this.em = em;
        this.em2=em2;
        this.em3 = em3;
    }

    private EntityManager em,em2,em3;

    @Bean
    public MemberService memberService(){return new MemberService(memberRepository());}
    @Bean
    public MemberRepository memberRepository(){
        return new JpaMemberRepository(em);
    }

    @Bean
    public GameService gameService() {
        return new GameService(answerRepository(), gameRepository());
    }

    @Bean
    public AnswerRepository answerRepository() {
        return new JpaAnswerRepository(em2);
    }
    @Bean
    public ManagerService managerService(){return new ManagerService(answerRepository(), gameRepository());}

    @Bean
    public GameRepository gameRepository(){ return new JpaGameRepository(em3);}
}
