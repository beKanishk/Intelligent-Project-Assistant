package com.assistant.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.assistant.dto.AiResponse;
import com.assistant.dto.MessageRequest;
import com.assistant.service.AiService;
import com.assistant.service.MessageService;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private AiService aiService;

    @MessageMapping("/chat/{sessionId}") 
    @SendTo("/topic/session/{sessionId}") 
    public MessageRequest sendMessage(
            @DestinationVariable Long sessionId,
            @Payload MessageRequest message,
            Principal principal
    ) {
        String jwt = extractJwtFromPrincipal(principal);

        // Ensure sessionId from path is used
        message.setSessionId(sessionId);

        // Save user's message
        messageService.handleIncomingMessage(message, jwt);

        // Call AI agent
        AiResponse aiReply = aiService.callAgent(message);

        // Save AI reply
        MessageRequest aiMessage = new MessageRequest();
        aiMessage.setSessionId(sessionId);
        aiMessage.setRole("assistant");
        aiMessage.setContent(aiReply.getResponse());
        aiMessage.setTools(aiReply.getTool_used());
        aiMessage.setUserId(message.getUserId());

        messageService.handleIncomingMessage(aiMessage, jwt);

        // Return AI's reply so all subscribers in this session get it
        return aiMessage;
    }

    private String callAiAgent(String userMessage, List<String> tools) {
        // Connect to AI backend / Python API / local method
        return "AI says hello to: " + userMessage;
    }

    private String extractJwtFromPrincipal(Principal principal) {
        // Extract JWT from Principal (depends on your security setup)
        return "";
    }
}
