package com.example.guess_music.service;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.MemberDetail;
import com.example.guess_music.domain.auth.Role;
import com.example.guess_music.domain.userInfo.GoogleUserInfo;
import com.example.guess_music.domain.userInfo.NaverUserInfo;
import com.example.guess_music.domain.userInfo.OAuthUserInfo;
import com.example.guess_music.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private MemberRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomOAuth2UserService(MemberRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser (OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuthUserInfo oAuthUserInfo=null;
        String provider = userRequest.getClientRegistration().getRegistrationId();    // google or naver ...
        if(provider.equals("google")){
            oAuthUserInfo=new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(provider.equals("naver")){
            oAuthUserInfo=new NaverUserInfo(oAuth2User.getAttributes());
        }
        String providerId = oAuthUserInfo.getProviderId();
        String username = provider+"_"+providerId;  			// 사용자가 입력한 적은 없지만 만들어준다

        String uuid = UUID.randomUUID().toString().substring(0, 6);
        String password = bCryptPasswordEncoder.encode("패스워드"+uuid);  // 사용자가 입력한 적은 없지만 만들어준다

        String email = oAuthUserInfo.getEmail();
        Role role = Role.ROLE_USER;
        Optional<Member> opt = userRepository.findbyUsername(username);
        //DB에 없는 사용자라면 회원가입처리
        if(opt.isPresent()){
            return new MemberDetail(opt.get(),oAuthUserInfo);
        }else{
            Member build = Member.oauth2Register().username(username).password(password).email(email).role(role)
                    .provider(provider).providerId(providerId)
                    .build();
            userRepository.save(build);
            return new MemberDetail(build, oAuthUserInfo);
        }

    }

}
