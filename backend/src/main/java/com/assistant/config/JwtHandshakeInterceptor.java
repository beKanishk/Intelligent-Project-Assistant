package com.assistant.config;

import com.assistant.security.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            // Try to get JWT from query parameter first
            String jwt = servletRequest.getServletRequest().getParameter("token");
            
            // If not in query param, try Authorization header
            if (jwt == null) {
                String authHeader = servletRequest.getServletRequest().getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    jwt = authHeader.substring(7);
                }
            }

            System.out.println("Raw JWT: " + (jwt != null ? jwt.substring(0, Math.min(jwt.length(), 10)) + "..." : "null"));

            if (jwt != null) {
                try {
                    if (jwtUtil.validateToken(jwt)) {
                        String email = jwtUtil.extractEmail(jwt);
                        attributes.put("email", email);
                        System.out.println("JWT validated for user: " + email);
                        return true;
                    } else {
                        System.out.println("JWT validation failed");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid JWT: " + e.getMessage());
                }
            } else {
                System.out.println("JWT token not found in WebSocket handshake request");
            }
        }
        return false; // reject handshake if no valid JWT
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Optional: Logging
    }
}
