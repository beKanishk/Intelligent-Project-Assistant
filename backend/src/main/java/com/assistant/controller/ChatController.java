//package com.assistant.controller;
//
//import java.security.Principal;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.assistant.dto.AiResponse;
//import com.assistant.dto.ContinueRequest;
//import com.assistant.dto.MessageRequest;
//import com.assistant.service.AiService;
//import com.assistant.service.MessageService;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@RestController
//@RequestMapping("/api")
//public class ChatController {
//
//    @Autowired
//    private MessageService messageService;
//    
//    @Autowired
//    private AiService aiService;
//    
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//    
//    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
//
////    @MessageMapping("/chat/{sessionId}") 
////    @SendTo("/topic/session/{sessionId}") 
////    public MessageRequest sendMessage(
////            @DestinationVariable Long sessionId,
////            @Payload MessageRequest message,
////            Principal principal
////    ) {
////        String jwt = extractJwtFromPrincipal(principal);
////
////        // Ensure sessionId from path is used
////        message.setSessionId(sessionId);
////
////        // Save user's message
////        messageService.handleIncomingMessage(message, jwt);
////
////        // Call AI agent
////        AiResponse aiReply = aiService.callAgent(message);
////
////        // Save AI reply
////        MessageRequest aiMessage = new MessageRequest();
////        aiMessage.setSessionId(sessionId);
////        aiMessage.setRole("assistant");
////        aiMessage.setContent(aiReply.getResponse());
////        aiMessage.setTools(aiReply.getTool_used());
////        aiMessage.setUserId(message.getUserId());
////
////        messageService.handleIncomingMessage(aiMessage, jwt);
////
////        // Return AI's reply so all subscribers in this session get it
////        return aiMessage;
////    }
//    
//    
////    @MessageMapping("/chat/{sessionId}")
////    public void sendMessage(
////            @DestinationVariable String sessionId,
////            @Payload MessageRequest messageRequest,
////            Principal principal
////    ) {
////        String senderEmail = principal.getName(); // From JWT handshake
////        messageRequest.setSessionId(sessionId);
////
////        // Save user's message + maybe return a DTO for frontend
////        messageService.handleIncomingMessage(messageRequest, senderEmail);
////        
////        AiResponse aiReply = aiService.callAgent(messageRequest);
////        // Send back to sender so their own chat updates
////        
////
////        // Call AI
//////        String aiReply = callAiAgent(messageRequest.getContent(), messageRequest.getTools());
////
////        // Save AI message
////        MessageRequest aiMessage = new MessageRequest();
////        aiMessage.setSessionId(sessionId);
////        aiMessage.setRole("assistant");
////        aiMessage.setContent(aiReply.getResponse());
////        aiMessage.setTools(aiReply.getTool_used());
////        aiMessage.setUserId(messageRequest.getUserId());
////
////        messageService.handleIncomingMessage(aiMessage, senderEmail);
////        
//////        MessageResponse aiMsgResponse = messageService.handleIncomingMessage(senderEmail, aiMessageReq);
////
////        // Send AI reply only to this sender (or to all in the session if multi-user)
//////        messagingTemplate.convertAndSendToUser(senderEmail, "/queue/session/" + sessionId, aiMsgResponse);
////        
////        messagingTemplate.convertAndSendToUser(senderEmail, "/queue/session/" + sessionId, aiReply);
////    }
//    
//    
////    @PostMapping("/send/{sessionId}")
////    public AiResponse sendMessage(
////            @PathVariable String sessionId,
////            @RequestBody MessageRequest messageRequest,
////            Principal principal
////    ) {
////    	String senderEmail = principal.getName();
////        messageRequest.setSessionId(sessionId);
////
////        // Save user's message
////        messageService.handleIncomingMessage(messageRequest, senderEmail);
////
////        // Call AI
////        AiResponse aiReply = aiService.callAgent(messageRequest);
////
////        // Save AI reply
////        MessageRequest aiMessage = new MessageRequest();
////        aiMessage.setSessionId(sessionId);
////        aiMessage.setRole("assistant");
////        aiMessage.setContent(aiReply.getResponse());
////        aiMessage.setTools(aiReply.getToolUsed());
////        aiMessage.setUserId(messageRequest.getUserId());
////
////        messageService.handleIncomingMessage(aiMessage, senderEmail);
////
////
////        // Return AI's reply (frontend will display user message immediately, then append AI reply)
////        return aiReply;
////    }
//
//    
//    @PostMapping("/send/{sessionId}")
//    public AiResponse sendMessage(
//            @PathVariable String sessionId,
//            @RequestBody MessageRequest messageRequest,
//            Principal principal
//    ) {
//        String senderEmail = principal.getName();
//        messageRequest.setSessionId(sessionId);
//
//        // Save user's message
//        messageService.handleIncomingMessage(messageRequest, senderEmail);
//
//        // Call AI
//        AiResponse aiReply = aiService.callAgent(messageRequest);
//
//        // Handle different response scenarios
//        if (aiReply.isPaused() && aiReply.getUserInputRequired() != null) {
//            // HITL: Agent is paused and waiting for user input
//            log.info("Agent paused for user input. Session: {}, RunId: {}", sessionId, aiReply.getRunId());
//            
//            // Save AI reply with paused status
//            MessageRequest aiMessage = new MessageRequest();
//            aiMessage.setSessionId(sessionId);
//            aiMessage.setRole("assistant");
//            aiMessage.setContent(aiReply.getResponse() + "\n\n[System: Waiting for user input]");
//            aiMessage.setTools(aiReply.getToolUsed());
//            aiMessage.setUserId(messageRequest.getUserId());
//            aiMessage.setPaused(true); // Assuming MessageRequest has this field
//            
//            messageService.handleIncomingMessage(aiMessage, senderEmail);
//            
//            // Return the paused response (frontend should show input form)
//            return aiReply;
//            
//        } else if (aiReply.isNeedsConfirmation()) {
//            // Natural language confirmation request
//            log.info("Agent requesting confirmation. Session: {}", sessionId);
//            
//            // Save AI reply with confirmation flag
//            MessageRequest aiMessage = new MessageRequest();
//            aiMessage.setSessionId(sessionId);
//            aiMessage.setRole("assistant");
//            aiMessage.setContent(aiReply.getResponse());
//            aiMessage.setTools(aiReply.getToolUsed());
//            aiMessage.setUserId(messageRequest.getUserId());
//            aiMessage.setNeedsConfirmation(true); // Assuming MessageRequest has this field
//            
//            messageService.handleIncomingMessage(aiMessage, senderEmail);
//            
//            return aiReply;
//            
//        } else if (aiReply.isError()) {
//            // Handle error responses
//            log.error("AI Agent error. Session: {}, Error: {}", sessionId, aiReply.getResponse());
//            
//            MessageRequest aiMessage = new MessageRequest();
//            aiMessage.setSessionId(sessionId);
//            aiMessage.setRole("assistant");
//            aiMessage.setContent("❌ " + aiReply.getResponse());
//            aiMessage.setTools(aiReply.getToolUsed());
//            aiMessage.setUserId(messageRequest.getUserId());
//            
//            messageService.handleIncomingMessage(aiMessage, senderEmail);
//            
//            return aiReply;
//            
//        } else {
//            // Normal successful response
//            log.info("Normal AI response. Session: {}, Tools: {}", sessionId, aiReply.getToolUsed());
//            
//            // Save AI reply normally
//            MessageRequest aiMessage = new MessageRequest();
//            aiMessage.setSessionId(sessionId);
//            aiMessage.setRole("assistant");
//            aiMessage.setContent(aiReply.getResponse());
//            aiMessage.setTools(aiReply.getToolUsed());
//            aiMessage.setUserId(messageRequest.getUserId());
//
//            messageService.handleIncomingMessage(aiMessage, senderEmail);
//
//            return aiReply;
//        }
//    }
//    
//    @PostMapping("/continue/{sessionId}")
//    public AiResponse continueRun(
//            @PathVariable String sessionId,
//            @RequestBody ContinueRequest continueRequest,
//            Principal principal
//    ) {
//        String senderEmail = principal.getName();
//        
//        try {
//            log.info("Continuing paused run. Session: {}, RunId: {}", sessionId, continueRequest.getRunId());
//            
//            // Call AI service to continue the paused run
//            AiResponse aiReply = aiService.continueAgent(continueRequest);
//            
//            // Save the continuation response
//            MessageRequest aiMessage = new MessageRequest();
//            aiMessage.setSessionId(sessionId);
//            aiMessage.setRole("assistant");
//            aiMessage.setContent(aiReply.getResponse());
//            aiMessage.setTools(aiReply.getToolUsed());
//            aiMessage.setUserId(continueRequest.getUserId());
//            
//            messageService.handleIncomingMessage(aiMessage, senderEmail);
//            
//            return aiReply;
//            
//        } catch (Exception e) {
//            log.error("Error continuing run: {}", e.getMessage());
//            
//            AiResponse errorResponse = new AiResponse();
//            errorResponse.setToolUsed(List.of("Error"));
//            errorResponse.setResponse("Failed to continue execution: " + e.getMessage());
//            errorResponse.setContent("Execution continuation failed");
//            errorResponse.setError(true);
//            
//            return errorResponse;
//        }
//    }
//
//
//   
//}


package com.assistant.controller;

import com.assistant.dto.AiResponse;
import com.assistant.dto.ContinueRequest;
import com.assistant.dto.MessageRequest;
import com.assistant.dto.MessageResponse;
import com.assistant.dto.UserInputField;
import com.assistant.service.AiService;
import com.assistant.service.MessageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@Controller
@CrossOrigin(origins = "*")
public class ChatController {

    private final MessageService messageService;
    private final AiService aiService;
    private final SimpMessagingTemplate messagingTemplate;
    
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    
    public ChatController(MessageService messageService, AiService aiService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.aiService = aiService;
        this.messagingTemplate = messagingTemplate;
    }

    // WebSocket endpoint for sending messages
    @MessageMapping("/chat/{sessionId}")
    public void sendMessage(
            @DestinationVariable String sessionId,
            @Payload MessageRequest messageRequest,
            Principal principal
    ) {
        String senderEmail = principal.getName();
        messageRequest.setSessionId(sessionId);

        log.info("Received WebSocket message for session: {} from user: {}", sessionId, senderEmail);

        try {
            // Save user's message
            messageService.handleIncomingMessage(messageRequest, senderEmail);

            // Send user message confirmation back to frontend
            MessageResponse userMsgResponse = new MessageResponse();
            userMsgResponse.setId(System.currentTimeMillis() + "_user");
            userMsgResponse.setType("user");
            userMsgResponse.setContent(messageRequest.getContent());
            userMsgResponse.setSessionId(sessionId);
            userMsgResponse.setTimestamp(System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                senderEmail, 
                "/queue/session/" + sessionId, 
                userMsgResponse
            );

            // Call AI agent
            AiResponse aiReply = aiService.callAgent(messageRequest);

            // Process AI response and send back
            processAndSendAiResponse(aiReply, sessionId, senderEmail, messageRequest.getUserId());

        } catch (Exception e) {
            log.error("Error processing WebSocket message: {}", e.getMessage(), e);
            
            // Send error message back to user
            MessageResponse errorResponse = new MessageResponse();
            errorResponse.setId(System.currentTimeMillis() + "_error");
            errorResponse.setType("error");
            errorResponse.setContent("Sorry, there was an error processing your message: " + e.getMessage());
            errorResponse.setSessionId(sessionId);
            errorResponse.setTimestamp(System.currentTimeMillis());
            errorResponse.setError(true);

            messagingTemplate.convertAndSendToUser(
                senderEmail, 
                "/queue/session/" + sessionId, 
                errorResponse
            );
        }
    }

    // WebSocket endpoint for continuing agent runs
    @MessageMapping("/continue/{sessionId}")
    public void continueAgent(
            @DestinationVariable String sessionId,
            @Payload ContinueRequest continueRequest,
            Principal principal
    ) {
        String senderEmail = principal.getName();
        
        log.info("Continuing agent run for session: {} from user: {}", sessionId, senderEmail);

        try {
            // Call AI service to continue
            AiResponse aiReply = aiService.continueAgent(continueRequest);

            // Process and send AI response
            processAndSendAiResponse(aiReply, sessionId, senderEmail, continueRequest.getUserId());

        } catch (Exception e) {
            log.error("Error continuing agent run: {}", e.getMessage(), e);

            MessageResponse errorResponse = new MessageResponse();
            errorResponse.setId(System.currentTimeMillis() + "_error");
            errorResponse.setType("error");
            errorResponse.setContent("Failed to continue execution: " + e.getMessage());
            errorResponse.setSessionId(sessionId);
            errorResponse.setTimestamp(System.currentTimeMillis());
            errorResponse.setError(true);

            messagingTemplate.convertAndSendToUser(
                senderEmail, 
                "/queue/session/" + sessionId, 
                errorResponse
            );
        }
    }

    private void processAndSendAiResponse(AiResponse aiReply, String sessionId, String senderEmail, Long userId) {
        MessageResponse aiMsgResponse = new MessageResponse();
        aiMsgResponse.setId(System.currentTimeMillis() + "_agent");
        aiMsgResponse.setType("agent");
        aiMsgResponse.setSessionId(sessionId);
        aiMsgResponse.setTimestamp(System.currentTimeMillis());
        aiMsgResponse.setRunId(aiReply.getRunId());
        aiMsgResponse.setToolUsed(aiReply.getToolUsed());

        if (aiReply.isPaused() && aiReply.getUserInputRequired() != null) {
            // HITL: Agent is paused and waiting for user input
            log.info("Agent paused for user input. Session: {}, RunId: {}", sessionId, aiReply.getRunId());
            
            aiMsgResponse.setContent(aiReply.getResponse());
            aiMsgResponse.setPaused(true);
            aiMsgResponse.setUserInputRequired(true);
            
            // Convert Map<String, Object> to UserInputField objects
            List<UserInputField> userInputFields = convertToUserInputFields(aiReply.getUserInputRequired());
            aiMsgResponse.setUserInputFields(userInputFields);

            // Save AI message with paused status
            saveAiMessage(sessionId, aiReply, senderEmail, userId, true);

        } else if (aiReply.isNeedsConfirmation()) {
            // Natural language confirmation request
            log.info("Agent requesting confirmation. Session: {}", sessionId);
            
            aiMsgResponse.setContent(aiReply.getResponse());
            aiMsgResponse.setNeedsConfirmation(true);

            saveAiMessage(sessionId, aiReply, senderEmail, userId, false);

        } else if (aiReply.isError()) {
            // Handle error responses
            log.error("AI Agent error. Session: {}, Error: {}", sessionId, aiReply.getResponse());
            
            aiMsgResponse.setContent("❌ " + aiReply.getResponse());
            aiMsgResponse.setError(true);

            saveAiMessage(sessionId, aiReply, senderEmail, userId, false);

        } else {
            // Normal successful response
            log.info("Normal AI response. Session: {}, Tools: {}", sessionId, aiReply.getToolUsed());
            
            aiMsgResponse.setContent(aiReply.getResponse());

            saveAiMessage(sessionId, aiReply, senderEmail, userId, false);
        }

        // Send AI response back to user
        messagingTemplate.convertAndSendToUser(
            senderEmail, 
            "/queue/session/" + sessionId, 
            aiMsgResponse
        );
    }

    // Helper method to convert Map<String, Object> to UserInputField
    private List<UserInputField> convertToUserInputFields(List<Map<String, Object>> userInputRequired) {
        if (userInputRequired == null) {
            return null;
        }
        
        return userInputRequired.stream()
            .map(this::mapToUserInputField)
            .collect(java.util.stream.Collectors.toList());
    }

    private UserInputField mapToUserInputField(Map<String, Object> fieldMap) {
        UserInputField field = new UserInputField();
        
        field.setFieldName((String) fieldMap.get("field_name"));
        field.setFieldDescription((String) fieldMap.get("field_description"));
        field.setFieldType((String) fieldMap.get("field_type"));
        
        // Handle optional fields
        if (fieldMap.containsKey("required")) {
            field.setRequired((Boolean) fieldMap.getOrDefault("required", false));
        }
        
        if (fieldMap.containsKey("default_value")) {
            field.setDefaultValue((String) fieldMap.get("default_value"));
        }
        
        if (fieldMap.containsKey("options")) {
            @SuppressWarnings("unchecked")
            List<String> options = (List<String>) fieldMap.get("options");
            field.setOptions(options);
        }
        
        return field;
    }


    private void saveAiMessage(String sessionId, AiResponse aiReply, String senderEmail, Long userId, boolean paused) {
        MessageRequest aiMessage = new MessageRequest();
        aiMessage.setSessionId(sessionId);
        aiMessage.setRole("assistant");
        aiMessage.setContent(aiReply.getResponse());
        aiMessage.setTools(aiReply.getToolUsed());
        aiMessage.setUserId(userId);
        aiMessage.setPaused(paused);

        messageService.handleIncomingMessage(aiMessage, senderEmail);
    }

    // Keep REST endpoints as backup/fallback
    @PostMapping("/api/chat/{sessionId}")
    @ResponseBody
    public AiResponse sendMessageRest(
            @PathVariable String sessionId,
            @RequestBody MessageRequest messageRequest,
            Principal principal
    ) {
        // Your existing REST implementation as fallback
        return handleRestMessage(sessionId, messageRequest, principal);
    }

    @PostMapping("/api/continue/{sessionId}")
    @ResponseBody
    public AiResponse continueAgentRest(
            @PathVariable String sessionId,
            @RequestBody ContinueRequest continueRequest,
            Principal principal
    ) {
        // Your existing REST implementation as fallback
        return handleRestContinue(sessionId, continueRequest, principal);
    }

    // Your existing REST methods here...
    private AiResponse handleRestMessage(String sessionId, MessageRequest messageRequest, Principal principal) {
        // Your existing POST implementation
        return new AiResponse(); // placeholder
    }

    private AiResponse handleRestContinue(String sessionId, ContinueRequest continueRequest, Principal principal) {
        // Your existing POST implementation
        return new AiResponse(); // placeholder
    }
}

