package com.example.guess_music;

import org.apache.catalina.Context;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer() {
        return (TomcatServletWebServerFactory container) -> {
            container.addContextCustomizers((Context context) -> {
                //ec2서버용
                //context.setDocBase("/home/ubuntu/audio");
                //local테스트 용
                context.setDocBase("/Users/sin-wongyun/Desktop/guessAudio");
                context.setPath("/");
                context.setReloadable(true);
            });
        };
    }
}
