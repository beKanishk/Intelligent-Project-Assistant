package com.assistant.chat.controller;


import com.assistant.chat.client.AiClient;
import com.assistant.chat.client.MessageClient;
import com.assistant.chat.dto.*;
import com.assistant.chat.token.TokenContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    @Lazy
    private AiClient aiClient;

    @Autowired
    @Lazy
    private MessageClient messageClient;

    // WebSocket endpoint for sending messages
    @MessageMapping("/chat/{sessionId}")
    public void sendMessage(
            @DestinationVariable String sessionId,
            @Payload MessageRequest messageRequest,
            Principal principal, SimpMessageHeaderAccessor headerAccessor
    ) {

        log.info("🔥🔥🔥 MESSAGE RECEIVED! 🔥🔥🔥");
        log.info("Session ID: {}", sessionId);
        log.info("Content: {}", messageRequest.getContent());
        log.info("User ID: {}", messageRequest.getUserId());
        log.info("Principal: {}", principal != null ? principal.getName() : "null");
        String senderEmail = principal.getName();

        messageRequest.setSessionId(sessionId);

        log.info("Received WebSocket message for session: {} from user: {}", sessionId, senderEmail);

        //Setting jwt for feign client
        String jwt = (String) headerAccessor.getSessionAttributes().get("jwt");
        if (jwt == null) throw new RuntimeException("JWT not found in WebSocket session");

            // Save user's message
//            messageClient.handleIncomingMessage(messageRequest, senderEmail);

            try {
                TokenContext.setToken(jwt);
                messageClient.handleIncomingMessage(messageRequest, senderEmail);
            } finally {
                TokenContext.clear(); // clear after Feign call
            }
        try {
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
            log.info("Jwt before ai client {}", jwt);

            try {
                TokenContext.setToken(jwt);
                AiResponse aiReply = aiClient.callAgent(messageRequest);
                processAndSendAiResponse(aiReply, sessionId, senderEmail, messageRequest.getUserId(), headerAccessor);
            } finally {
                TokenContext.clear(); // clear after Feign call
            }


            // Process AI response and send back


        } catch (Exception e) {
            log.error("Error processing WebSocket message: {} {}", e.getMessage(), e.getStackTrace());

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

    @MessageMapping("/echo")
    @SendTo("/topic/echo")
    public MessageRequest echo(MessageRequest message) {
        System.out.println("Echo received: " + message.getContent());
        return message;
    }


    // WebSocket endpoint for continuing agent runs
    @MessageMapping("/continue/{sessionId}")
    public void continueAgent(
            @DestinationVariable String sessionId,
            @Payload ContinueRequest continueRequest,
            Principal principal, SimpMessageHeaderAccessor headerAccessor
    ) {
        String senderEmail = principal.getName();

        log.info("Continuing agent run for session: {} from user: {}", sessionId, senderEmail);
        String jwt = (String) headerAccessor.getSessionAttributes().get("jwt");
        if (jwt == null) throw new RuntimeException("JWT not found in WebSocket session in continue agent function");

        try{
            // Call AI service to continue
            try {
                TokenContext.setToken(jwt);
                AiResponse aiReply = aiClient.continueAgent(continueRequest);

                // Process and send AI response
                processAndSendAiResponse(aiReply, sessionId, senderEmail, continueRequest.getUserId(), headerAccessor);
            } finally {
                TokenContext.clear(); // clear after Feign call
            }





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

    private void processAndSendAiResponse(AiResponse aiReply, String sessionId, String senderEmail, Long userId, SimpMessageHeaderAccessor headerAccessor) {
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
            saveAiMessage(sessionId, aiReply, senderEmail, userId, true, headerAccessor);

        } else if (aiReply.isNeedsConfirmation()) {
            // Natural language confirmation request
            log.info("Agent requesting confirmation. Session: {}", sessionId);

            aiMsgResponse.setContent(aiReply.getResponse());
            aiMsgResponse.setNeedsConfirmation(true);

            saveAiMessage(sessionId, aiReply, senderEmail, userId, false, headerAccessor);

        } else if (aiReply.isError()) {
            // Handle error responses
            log.error("AI Agent error. Session: {}, Error: {}", sessionId, aiReply.getResponse());

            aiMsgResponse.setContent("❌ " + aiReply.getResponse());
            aiMsgResponse.setError(true);

            saveAiMessage(sessionId, aiReply, senderEmail, userId, false, headerAccessor);

        } else {
            // Normal successful response
            log.info("Normal AI response. Session: {}, Tools: {}", sessionId, aiReply.getToolUsed());

            aiMsgResponse.setContent(aiReply.getResponse());

            saveAiMessage(sessionId, aiReply, senderEmail, userId, false, headerAccessor);
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


    private void saveAiMessage(String sessionId, AiResponse aiReply, String senderEmail, Long userId, boolean paused, SimpMessageHeaderAccessor headerAccessor) {
        MessageRequest aiMessage = new MessageRequest();
        aiMessage.setSessionId(sessionId);
        aiMessage.setRole("assistant");
        aiMessage.setContent(aiReply.getResponse());
        aiMessage.setTools(aiReply.getToolUsed());
        aiMessage.setUserId(userId);
        aiMessage.setPaused(paused);

        String jwt = (String) headerAccessor.getSessionAttributes().get("jwt");
        if (jwt == null) throw new RuntimeException("JWT not found in WebSocket session in save ai message function");

        try {
            TokenContext.setToken(jwt);
            messageClient.handleIncomingMessage(aiMessage, senderEmail);
        } finally {
            TokenContext.clear(); // clear after Feign call
        }

    }
}
