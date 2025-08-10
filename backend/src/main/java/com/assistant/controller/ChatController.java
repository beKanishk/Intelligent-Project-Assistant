package com.assistant.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assistant.dto.AiResponse;
import com.assistant.dto.MessageRequest;
import com.assistant.service.AiService;
import com.assistant.service.MessageService;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private AiService aiService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

//    @MessageMapping("/chat/{sessionId}") 
//    @SendTo("/topic/session/{sessionId}") 
//    public MessageRequest sendMessage(
//            @DestinationVariable Long sessionId,
//            @Payload MessageRequest message,
//            Principal principal
//    ) {
//        String jwt = extractJwtFromPrincipal(principal);
//
//        // Ensure sessionId from path is used
//        message.setSessionId(sessionId);
//
//        // Save user's message
//        messageService.handleIncomingMessage(message, jwt);
//
//        // Call AI agent
//        AiResponse aiReply = aiService.callAgent(message);
//
//        // Save AI reply
//        MessageRequest aiMessage = new MessageRequest();
//        aiMessage.setSessionId(sessionId);
//        aiMessage.setRole("assistant");
//        aiMessage.setContent(aiReply.getResponse());
//        aiMessage.setTools(aiReply.getTool_used());
//        aiMessage.setUserId(message.getUserId());
//
//        messageService.handleIncomingMessage(aiMessage, jwt);
//
//        // Return AI's reply so all subscribers in this session get it
//        return aiMessage;
//    }
    
    
//    @MessageMapping("/chat/{sessionId}")
//    public void sendMessage(
//            @DestinationVariable Long sessionId,
//            @Payload MessageRequest messageRequest,
//            Principal principal
//    ) {
//        String senderEmail = principal.getName(); // From JWT handshake
//        messageRequest.setSessionId(sessionId);
//
//        // Save user's message + maybe return a DTO for frontend
//        messageService.handleIncomingMessage(messageRequest, senderEmail);
//        
//        AiResponse aiReply = aiService.callAgent(messageRequest);
//        // Send back to sender so their own chat updates
//        
//
//        // Call AI
////        String aiReply = callAiAgent(messageRequest.getContent(), messageRequest.getTools());
//
//        // Save AI message
//        MessageRequest aiMessage = new MessageRequest();
//        aiMessage.setSessionId(sessionId);
//        aiMessage.setRole("assistant");
//        aiMessage.setContent(aiReply.getResponse());
//        aiMessage.setTools(aiReply.getTool_used());
//        aiMessage.setUserId(messageRequest.getUserId());
//
//        messageService.handleIncomingMessage(aiMessage, senderEmail);
//        
////        MessageResponse aiMsgResponse = messageService.handleIncomingMessage(senderEmail, aiMessageReq);
//
//        // Send AI reply only to this sender (or to all in the session if multi-user)
////        messagingTemplate.convertAndSendToUser(senderEmail, "/queue/session/" + sessionId, aiMsgResponse);
//        
//        messagingTemplate.convertAndSendToUser(senderEmail, "/queue/session/" + sessionId, aiReply);
//    }
    
    
    @PostMapping("/send/{sessionId}")
    public AiResponse sendMessage(
            @PathVariable Long sessionId,
            @RequestBody MessageRequest messageRequest,
            Principal principal
    ) {
    	String senderEmail = principal.getName();
        messageRequest.setSessionId(sessionId);

        // Save user's message
        messageService.handleIncomingMessage(messageRequest, senderEmail);

        // Call AI
        AiResponse aiReply = aiService.callAgent(messageRequest);

        // Save AI reply
        MessageRequest aiMessage = new MessageRequest();
        aiMessage.setSessionId(sessionId);
        aiMessage.setRole("assistant");
        aiMessage.setContent(aiReply.getResponse());
        aiMessage.setTools(aiReply.getTool_used());
        aiMessage.setUserId(messageRequest.getUserId());

        messageService.handleIncomingMessage(aiMessage, senderEmail);


        // Return AI's reply (frontend will display user message immediately, then append AI reply)
        return aiReply;
    }

   
}
