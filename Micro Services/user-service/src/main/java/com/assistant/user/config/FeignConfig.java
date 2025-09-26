package com.assistant.user.config;

import com.assistant.user.token.TokenContext;
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
                template.header("Authorization", token);
            }
        };
    }
}

