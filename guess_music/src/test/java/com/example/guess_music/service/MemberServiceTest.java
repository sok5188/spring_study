package com.example.guess_music.service;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.Role;
import com.example.guess_music.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    MemberService memberService;
    private Member getMember1() {
        Member member=new Member();
        member.setUsername("testUser1");
        member.setName("testUser1");
        member.setPassword("testPWD");
        member.setEmail("lyhxr@example.com");
        member.setRole(Role.ROLE_USER);

        return member;
    }


    @Test
    void 회원가입() {
        //given
        Member member = getMember1();
        given(memberRepository.save(member)).willReturn(member);

        //when
        String username = memberService.join(member);

        //then
        assertThat(username).isEqualTo(member.getUsername());
    }


    @Test
    public void 회원가입_실패(){
        //given
        Member member= getMember1();
        Member member2= getMember1();

        given(memberRepository.save(member)).willReturn(member);

        memberService.join(member);

        given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));

        //when
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        //then
        assertThat(e.getMessage()).isEqualTo("already exist id");
    }

    @Test
    void 전체회원조회() {
        //given
        Member member=getMember1();
        List<Member> members=new ArrayList<>();
        members.add(member);
        given(memberRepository.save(member)).willReturn(member);
        given(memberRepository.findAll()).willReturn(members);
        //when
        memberService.join(member);
        List<Member> findMembers = memberService.findMembers();

        //then
        assertThat(findMembers.size()).isEqualTo(1);
    }

    @Test
    void 특정회원조회_findOne() {
        //given
        Member member=getMember1();
        //given(memberRepository.save(member)).willReturn(member);
        given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));
        //when
        Optional<Member> opt = memberService.findOne(member.getUsername());
        //then
        assertThat(opt).isNotNull();
        assertThat(opt.get().getUsername()).isEqualTo(member.getUsername());
    }
    @Test
    void 특정회원조회_loadUser() {
        //given
        Member member=getMember1();
        //given(memberRepository.save(member)).willReturn(member);
        given(memberRepository.findByUsername(member.getUsername())).willReturn(Optional.of(member));
        //when
        UserDetails opt = memberService.loadUserByUsername(member.getUsername());
        //then
        assertThat(opt.getUsername()).isEqualTo(member.getUsername());
    }

}