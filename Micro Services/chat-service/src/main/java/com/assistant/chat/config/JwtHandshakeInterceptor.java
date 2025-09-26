package com.assistant.chat.config;

import com.assistant.chat.token.TokenContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            if (request instanceof ServletServerHttpRequest servletRequest) {
                String jwt = servletRequest.getServletRequest().getParameter("token");
                log.info("Request URI: " + servletRequest.getServletRequest().getRequestURI());
                log.info("Token: " + jwt);

                // If not in query param, check Authorization header
                if (jwt == null) {
                    String authHeader = servletRequest.getServletRequest().getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        jwt = authHeader.substring(7);
                    }
                }

                if (jwt != null && jwtUtil.validateToken(jwt)) {
                    String email = jwtUtil.extractEmail(jwt);
                    attributes.put("email", email);
                    attributes.put("jwt", jwt);

                    // Save into TokenContext so Feign clients can use it
                    TokenContext.setToken(jwt);

                    System.out.println("✅ Handshake validated for: " + email);
                    return true;
                } else {
                    System.out.println("❌ Invalid or missing JWT during handshake");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Exception in beforeHandshake: " + e.getMessage());
            return false;
        }

        return false; // default reject
    }




    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Optional: Logging
    }
}
