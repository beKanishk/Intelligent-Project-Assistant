package com.assistant.chat.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final JwtHandshakeInterceptor jwtInterceptor;

    public WebSocketConfig(JwtHandshakeInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @PostConstruct
    public void init() {
        logger.info("🔥🔥🔥 WebSocketConfig INITIALIZED! 🔥🔥🔥");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        logger.info("Configuring message broker...");
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("Registering STOMP endpoints...");

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173")
                .addInterceptors(jwtInterceptor)
                .setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();

        //this is for normal websocket
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("http://localhost:5173")
//                .addInterceptors(jwtInterceptor)
//                .setHandshakeHandler(new CustomHandshakeHandler());

    }

    // Remove the JWT channel interceptor completely
    // Let the handshake interceptor handle authentication
}
