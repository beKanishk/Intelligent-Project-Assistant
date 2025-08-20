package com.assistant.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, 
                                      Map<String, Object> attributes) {
        // Get email from handshake attributes (set by interceptor)
        String email = (String) attributes.get("email");
        if (email != null) {
            return () -> email; // Return a Principal with the email
        }
        return null;
    }
}
