package com.example.guess_music.controller;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.Role;
import com.example.guess_music.domain.auth.SignInForm;
import com.example.guess_music.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private MemberService memberService;

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
        private void makeUser(String number) {
            String username="testUser";
            String name="testName";
            String email="testEmail";
            String password="testPassword";

            Member member=new Member();
            member.setUsername(username+number);
            member.setName(name+number);
            member.setEmail(email+number);
            member.setPassword(bCryptPasswordEncoder.encode(password+number));
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
        void 로그인_로그아웃_성공()throws Exception{
            makeUser("2");
            mockMvc.perform(post("/login").param("username","testUser2").param("password","testPassword2"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(authenticated());
            mockMvc.perform(post("/logout"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        void 로그인실패_비밀번호오류() throws Exception{
            makeUser("1");
            mockMvc.perform(formLogin().user("testUser1").password("testPassword22"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/auth/loginForm?error=true&exception=2"))
                    .andExpect(unauthenticated());
        }

        @Test
        void 로그인실패_중복로그인() throws Exception{
            makeUser("1");
            mockMvc.perform(post("/login").param("username","testUser1").param("password","testPassword1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(authenticated().withUsername("testUser1"));
            mockMvc.perform(formLogin().user("testUser1").password("testPassword1"))
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
        @WithMockUser(username = "testUser3", password = "testPassword3")
        void 로그인성공_재로그인시도() throws Exception{
            makeUser("3");
            mockMvc.perform(post("/login").param("username","testUser3").param("password","testPassword3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(authenticated().withUsername("testUser3"));

            mockMvc.perform(get("/auth/loginForm")).andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));
        }
        @Test
        void OAuth로그인성공() throws Exception {
            makeUser("1");
            session.setAttribute("username","testUser1");

            mockMvc.perform(get("/auth/oAuthUserCheck").session(session)).andDo(print())
                    .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
        }
        @Test
        void OAuth로그인_이름설정필요() throws Exception {
            String username="testUser";
            String email="testEmail";
            String password="testPassword";

            Member member=new Member();
            member.setUsername(username);
            member.setEmail(email);
            member.setPassword(bCryptPasswordEncoder.encode(password));
            member.setRole(Role.ROLE_USER);
            String join = memberService.join(member);

            session.setAttribute("username","testUser");

            mockMvc.perform(get("/auth/oAuthUserCheck").session(session)).andDo(print())
                    .andExpect(status().isOk()).andExpect(view().name("login/oAuthSignUp"));
        }
        @Test
        void OAuth로그인_이름설정완료() throws Exception{
            makeUser("1");
            session.setAttribute("username","testUser1");
            mockMvc.perform(post("/auth/oAuthSignUp").param("name","newTestName").session(session))
                    .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"));
            Optional<Member> testUser = memberService.findOne("testUser1");
            assertThat(testUser.isPresent()).isTrue();
            assertThat(testUser.get().getName()).isEqualTo("newTestName");
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
        void 회원가입성공() throws Exception {
            mockMvc.perform(post("/auth/signIn").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("username","testUser").param("password","testPassword")
                    .param("name","testName").param("email","testEmail"))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/auth/loginForm"));
        }
        @Test
        void 회원가입실패_중복아이디() throws Exception {
            mockMvc.perform(post("/auth/signIn").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username","testUser").param("password","testPassword")
                            .param("name","testName").param("email","testEmail"))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/auth/loginForm"));
            mockMvc.perform(post("/auth/signIn").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("username","testUser").param("password","testPassword")
                            .param("name","testName").param("email","testEmail"))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/auth/joinForm?error=true"));
            ;

        }
    }
    @Nested
    @DisplayName("접근 권한 테스트")
    class accessTest{
        @Test
        @WithAnonymousUser
        void 유저이상필요_실패() throws Exception {
            mockMvc.perform(get("/Game/roomList"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/?error=authenticationError"));
        }
        @Test
        @WithMockUser
        void 유저이상필요_성공() throws Exception {
            session.setAttribute("username","testUser");
            mockMvc.perform(get("/Game/test").session(session))
                    .andDo(print())
                    .andExpect(status().isOk()).andExpect(view().name("test"));
        }

        @Test
        @WithMockUser(roles = "USER")
        void 관리자이상필요_실패() throws Exception {
            mockMvc.perform(get("/manage"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/?error=accessDenied"));
        }
        @Test
        @WithMockUser(roles = "MANAGER")
        void 관리자이상필요_성공() throws Exception {
            mockMvc.perform(get("/manage")).andDo(print())
                    .andExpect(status().isOk()).andExpect(view().name("manage/manager"));
        }
    }
}