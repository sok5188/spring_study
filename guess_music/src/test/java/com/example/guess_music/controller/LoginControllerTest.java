package com.example.guess_music.controller;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.Role;
import com.example.guess_music.service.MemberService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private MemberService memberService;
//    @BeforeEach
//    public void setUp() {
//        mockMvc= MockMvcBuilders.webAppContextSetup(this.context).apply(springSecurity()).build();
//    }
    private MockHttpSession session;
    @BeforeEach
    public void beforeEach(){
        session=new MockHttpSession();
    }
    @AfterEach
    public void afterEach(){
        session.invalidate();
    }
    @Nested
    @DisplayName("로그인 관련 테스트")
    class LoginTest {
        private void makeUser() {
            Member member=new Member();
            member.setUsername("testUser");
            member.setName("testName");
            member.setEmail("testEmail");
            member.setPassword(bCryptPasswordEncoder.encode("testPassword"));
            member.setRole(Role.ROLE_USER);
            String join = memberService.join(member);
            System.out.println("join user name : "+join);
        }
        private void makeUser2() {
            Member member=new Member();
            member.setUsername("testUser2");
            member.setName("testName2");
            member.setEmail("testEmail2");
            member.setPassword(bCryptPasswordEncoder.encode("testPassword2"));
            member.setRole(Role.ROLE_USER);
            String join = memberService.join(member);
            System.out.println("join user name : "+join);
        }

        @Test
        void createLoginForm() throws Exception{

            mockMvc.perform(get("/auth/loginForm"))
                    .andExpect(status().isOk()).andExpect(view().name("login/createLoginForm"));
        }
        @Test
        void 로그인성공()throws Exception{
            makeUser2();
            mockMvc.perform(post("/login").param("username","testUser2").param("password","testPassword2"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(authenticated());
            mockMvc.perform(post("/logout").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }
        @Test
        void 로그아웃성공() throws Exception {
            mockMvc.perform(post("/logout").with(csrf()))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(unauthenticated());
            ;
        }

        @Test
        void 로그인실패_비밀번호오류() throws Exception{
            makeUser();
            mockMvc.perform(formLogin().user("testUser").password("testPassword22"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/auth/loginForm?error=true&exception=2"))
                    .andExpect(unauthenticated());
        }

        @Test
        void 로그인실패_중복로그인() throws Exception{
            makeUser();
            mockMvc.perform(post("/login").param("username","testUser").param("password","testPassword").session(session))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(authenticated().withUsername("testUser"));
            mockMvc.perform(formLogin().user("testUser").password("testPassword"))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/auth/loginForm?error=true&exception=6"))
                    .andExpect(unauthenticated())
            ;
            mockMvc.perform(post("/logout"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));

        }
        @Test
        void 로그인성공_재로그인시도() throws Exception{

        }
    }
    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {
        @Test
        void createSignInForm() throws Exception {
            mockMvc.perform(get("/auth/joinForm"))
                    .andExpect(status().isOk()).andExpect(view().name("login/createSignInForm"));
        }
        @Test
        void 회원가입성공() {

        }
        @Test
        void 회원가입실패_중복아이디() {

        }
    }

    @Test
    void accessDeny() {
    }

    @Test
    void oAuthUsercheck() {
    }

    @Test
    void oAuthSignUp() {
    }
}