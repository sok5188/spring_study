package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {
    @Autowired MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입() {
        //given
        Member member = new Member();
        //member.setName("sirong");
        member.setName("sirong3");

        //when
        Long saveId = memberService.join(member);
        //System.out.println("s id  "+ saveId);
        //System.out.println("get id :"+member.getId());
        //then
        Member findMember = memberService.findOne(saveId).get();
        //System.out.println("find id : "+findMember.getId());
        //System.out.println("find name: "+findMember.getName());
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

//    @Test
//    public void 중복_회원_예외() throws Exception {
//        //given
//        Member member = new Member();
//        member.setName("sisiro2");
//        Member member2 = new Member();
//        member2.setName("sisiro2");
//
//        //when
//        memberService.join(member);
//        //해당 예외가 터져야 테스트가 성공
//        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
//        assertThat(e.getMessage()).isEqualTo("already exist");
//
//        //이건 너무 길고 불편..
////        try{ memberService.join(member2);
////            fail();
////        }catch (IllegalStateException e){
////            assertThat(e.getMessage()).isEqualTo("already exist")
////        }
//        //then
//
//    }


}