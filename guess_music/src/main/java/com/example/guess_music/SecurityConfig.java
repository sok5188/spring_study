//package com.example.guess_music;
//
//
//import com.example.guess_music.service.MemberService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.provisioning.JdbcUserDetailsManager;
//import org.springframework.security.provisioning.UserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
//import javax.sql.DataSource;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@EnableWebSecurity
//@EnableGlobalMethodSecurity
//public class SecurityConfig  {
//    private MemberService memberService;
//    @Autowired
//    public SecurityConfig(MemberService memberService) {
//        this.memberService = memberService;
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        System.out.println("int datasource");
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
//                .build();
//    }
//    @Bean
//    public BCryptPasswordEncoder encodePWD(){ //비밀번호 암호화를 위해 사용 시큐리티는 비밀번호가 암호화 되있어야 사용가능하다
//        System.out.println("int encoder");
//        return new BCryptPasswordEncoder();   //회원가입할때 쓰면된다.
//    }
//    @Bean
//    public UserDetailsManager users(DataSource dataSource) {
//        System.out.println("int userdetails");
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("username")
//                .password("password")
//                .roles("USER")
//                .build();
//        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
//        users.createUser(user);
//        return users;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        System.out.println("in filter chain");
////        http.csrf().disable()
////                .authorizeHttpRequests((authz) -> {
////                            try {
////                                authz.requestMatchers("/","/login/**","/js/**","/css/**","/image/**").permitAll()
////                                        .anyRequest().authenticated()
////                                        .and()
////                                        .formLogin()
////                                        .loginPage("/login/createLoginForm").loginProcessingUrl("/login/proc").defaultSuccessUrl("/");
////                            } catch (Exception e) {
////                                throw new RuntimeException(e);
////                            }
////                        }
////                )
////                .httpBasic(withDefaults());
////        http.sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(false);
//
//        http
//                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers("/", "/home").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin((form) -> form
//                        .loginPage("/login/createLoginForm")
//                        .permitAll()
//                )
//                .logout((logout) -> logout.permitAll());
//
//        return http.build();
//    }
//
//
//}
