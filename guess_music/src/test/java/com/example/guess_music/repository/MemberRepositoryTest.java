package com.example.guess_music.repository;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    private Member getMember() {
        Member member=new Member();
        member.setUsername("testUser1");
        member.setName("testUser1");
        member.setPassword("testPWD");
        member.setEmail("lyhxr@example.com");
        member.setRole(Role.ROLE_USER);

        return member;
    }
    @Test
    void save() {
        //given
        Member member=getMember();
        //when
        Member save = memberRepository.save(member);
        //then
        assertThat(save.getUsername()).isEqualTo(member.getUsername());
    }
    @Test
    void findByUsername() {
        //given
        Member member=getMember();
        Member save = memberRepository.save(member);
        //when
        Optional<Member> opt = memberRepository.findByUsername(member.getUsername());
        //then
        assertThat(opt).isNotNull();
        assertThat(opt.get().getUsername()).isEqualTo(member.getUsername());
    }
    @Test
    void updateName() {
        //given
        Member member=getMember();
        memberRepository.save(member);
        //when
        memberRepository.updateName("newName",member.getUsername());
        //then
        Optional<Member> opt = memberRepository.findByUsername(member.getUsername());
        assertThat(opt).isNotNull();
        assertThat(opt.get().getName()).isEqualTo("newName");
    }
    @Test
    void findByName() {
        //given
        Member member=getMember();
        Member save = memberRepository.save(member);
        //when
        Optional<Member> opt = memberRepository.findByName(member.getName());
        //then
        assertThat(opt).isNotNull();
        assertThat(opt.get().getName()).isEqualTo(member.getName());
    }


}