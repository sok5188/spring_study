package com.example.guess_music.service;

import com.example.guess_music.domain.Member;
import com.example.guess_music.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository    memberRepository;

//    @Test
//    void 회원가입() {
//        //given
//        Member member=new Member();
//        member.setUsername("sirong_test");
//        member.setPassword("123");
//        member.setName("Tester");
//        //when
//        String saveId=memberService.join(member);
//        //then
//        Member findMember= memberService.findOne(saveId).get();
//        assertThat(member.getName()).isEqualTo(findMember.getName());
//    }

//    @Test
//    void 중복회원_예외() throws Exception{
//        Member member=new Member();
//        member.setName("test1");
//        member.setUsername("test1");
//        member.setPassword("123");
//
//        Member member2=new Member();
//        member2.setName("test1");
//        member2.setUsername("test1");
//        member2.setPassword("123");
//
//        memberService.join(member);
//
//        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
//        assertThat(e.getMessage()).isEqualTo("already exist id");
//    }
//
//    @Test
//    void 로그인(){
//        Member member=new Member();
//        member.setName("test1");
//        member.setUsername("test1");
//        member.setPassword("123");
//        memberService.join(member);
//
//        Optional<Member> test = memberService.findOne("test1");
//
//        assertThat(test.isPresent()).isEqualTo(true);
//        assertThat(test.get().getPassword()).isEqualTo("123");
//
//        //비밀번호 오류는 위에 비밀번호 확인하는 부분만 다르기에 이 테스트로 비밀번호 오류도 테스팅이 된 것이라 간주.
//    }
//
//    @Test
//    void 로그인_아이디오류(){
//        Member member=new Member();
//        member.setName("test1");
//        member.setUsername("test1");
//        member.setPassword("123");
//        memberService.join(member);
//
//        Optional<Member> test12345 = memberService.findOne("test12345");
//
//        assertThat(test12345.isPresent()).isEqualTo(false);
//    }


}
