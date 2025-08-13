package com.assistant.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assistant.dto.AiResponse;
import com.assistant.dto.ContinueRequest;
import com.assistant.dto.MessageRequest;
import com.assistant.service.AiService;
import com.assistant.service.MessageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private AiService aiService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

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
//            @DestinationVariable String sessionId,
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
    
    
//    @PostMapping("/send/{sessionId}")
//    public AiResponse sendMessage(
//            @PathVariable String sessionId,
//            @RequestBody MessageRequest messageRequest,
//            Principal principal
//    ) {
//    	String senderEmail = principal.getName();
//        messageRequest.setSessionId(sessionId);
//
//        // Save user's message
//        messageService.handleIncomingMessage(messageRequest, senderEmail);
//
//        // Call AI
//        AiResponse aiReply = aiService.callAgent(messageRequest);
//
//        // Save AI reply
//        MessageRequest aiMessage = new MessageRequest();
//        aiMessage.setSessionId(sessionId);
//        aiMessage.setRole("assistant");
//        aiMessage.setContent(aiReply.getResponse());
//        aiMessage.setTools(aiReply.getToolUsed());
//        aiMessage.setUserId(messageRequest.getUserId());
//
//        messageService.handleIncomingMessage(aiMessage, senderEmail);
//
//
//        // Return AI's reply (frontend will display user message immediately, then append AI reply)
//        return aiReply;
//    }

    
    @PostMapping("/send/{sessionId}")
    public AiResponse sendMessage(
            @PathVariable String sessionId,
            @RequestBody MessageRequest messageRequest,
            Principal principal
    ) {
        String senderEmail = principal.getName();
        messageRequest.setSessionId(sessionId);

        // Save user's message
        messageService.handleIncomingMessage(messageRequest, senderEmail);

        // Call AI
        AiResponse aiReply = aiService.callAgent(messageRequest);

        // Handle different response scenarios
        if (aiReply.isPaused() && aiReply.getUserInputRequired() != null) {
            // HITL: Agent is paused and waiting for user input
            log.info("Agent paused for user input. Session: {}, RunId: {}", sessionId, aiReply.getRunId());
            
            // Save AI reply with paused status
            MessageRequest aiMessage = new MessageRequest();
            aiMessage.setSessionId(sessionId);
            aiMessage.setRole("assistant");
            aiMessage.setContent(aiReply.getResponse() + "\n\n[System: Waiting for user input]");
            aiMessage.setTools(aiReply.getToolUsed());
            aiMessage.setUserId(messageRequest.getUserId());
            aiMessage.setPaused(true); // Assuming MessageRequest has this field
            
            messageService.handleIncomingMessage(aiMessage, senderEmail);
            
            // Return the paused response (frontend should show input form)
            return aiReply;
            
        } else if (aiReply.isNeedsConfirmation()) {
            // Natural language confirmation request
            log.info("Agent requesting confirmation. Session: {}", sessionId);
            
            // Save AI reply with confirmation flag
            MessageRequest aiMessage = new MessageRequest();
            aiMessage.setSessionId(sessionId);
            aiMessage.setRole("assistant");
            aiMessage.setContent(aiReply.getResponse());
            aiMessage.setTools(aiReply.getToolUsed());
            aiMessage.setUserId(messageRequest.getUserId());
            aiMessage.setNeedsConfirmation(true); // Assuming MessageRequest has this field
            
            messageService.handleIncomingMessage(aiMessage, senderEmail);
            
            return aiReply;
            
        } else if (aiReply.isError()) {
            // Handle error responses
            log.error("AI Agent error. Session: {}, Error: {}", sessionId, aiReply.getResponse());
            
            MessageRequest aiMessage = new MessageRequest();
            aiMessage.setSessionId(sessionId);
            aiMessage.setRole("assistant");
            aiMessage.setContent("❌ " + aiReply.getResponse());
            aiMessage.setTools(aiReply.getToolUsed());
            aiMessage.setUserId(messageRequest.getUserId());
            
            messageService.handleIncomingMessage(aiMessage, senderEmail);
            
            return aiReply;
            
        } else {
            // Normal successful response
            log.info("Normal AI response. Session: {}, Tools: {}", sessionId, aiReply.getToolUsed());
            
            // Save AI reply normally
            MessageRequest aiMessage = new MessageRequest();
            aiMessage.setSessionId(sessionId);
            aiMessage.setRole("assistant");
            aiMessage.setContent(aiReply.getResponse());
            aiMessage.setTools(aiReply.getToolUsed());
            aiMessage.setUserId(messageRequest.getUserId());

            messageService.handleIncomingMessage(aiMessage, senderEmail);

            return aiReply;
        }
    }
    
    @PostMapping("/continue/{sessionId}")
    public AiResponse continueRun(
            @PathVariable String sessionId,
            @RequestBody ContinueRequest continueRequest,
            Principal principal
    ) {
        String senderEmail = principal.getName();
        
        try {
            log.info("Continuing paused run. Session: {}, RunId: {}", sessionId, continueRequest.getRunId());
            
            // Call AI service to continue the paused run
            AiResponse aiReply = aiService.continueAgent(continueRequest);
            
            // Save the continuation response
            MessageRequest aiMessage = new MessageRequest();
            aiMessage.setSessionId(sessionId);
            aiMessage.setRole("assistant");
            aiMessage.setContent(aiReply.getResponse());
            aiMessage.setTools(aiReply.getToolUsed());
            aiMessage.setUserId(continueRequest.getUserId());
            
            messageService.handleIncomingMessage(aiMessage, senderEmail);
            
            return aiReply;
            
        } catch (Exception e) {
            log.error("Error continuing run: {}", e.getMessage());
            
            AiResponse errorResponse = new AiResponse();
            errorResponse.setToolUsed(List.of("Error"));
            errorResponse.setResponse("Failed to continue execution: " + e.getMessage());
            errorResponse.setContent("Execution continuation failed");
            errorResponse.setError(true);
            
            return errorResponse;
        }
    }


   
}
