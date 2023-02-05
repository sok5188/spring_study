package com.example.guess_music;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ChatConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(("/ws")).setAllowedOriginPatterns("*").withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        //클라이언트가 /queue나 /topic으로 시작되는 경로로 들어온다면 구독 중 이다
        registry.enableSimpleBroker("/queue","/topic");
        //메세지를 구독자에게 배포하는 경우 /app 하단의 경로로 배달?
        registry.setApplicationDestinationPrefixes("/app");
    }
}
