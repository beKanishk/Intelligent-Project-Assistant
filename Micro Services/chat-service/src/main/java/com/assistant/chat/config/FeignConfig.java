package com.assistant.chat.config;

import com.assistant.chat.token.TokenContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            String token = TokenContext.getToken();
            if (token != null) {
                template.header("Authorization", "Bearer " + token);
            }
        };
    }
}

