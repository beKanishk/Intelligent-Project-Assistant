//package com.assistant.service;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import com.assistant.controller.ChatController;
//import com.assistant.dto.AiResponse;
//import com.assistant.dto.ContinueRequest;
//import com.assistant.dto.MessageRequest;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Service
//public class AiService {
//    @Value("${python.ai.url:http://localhost:8001/assist}")
//    private String aiUrl;
//
//    private final RestTemplate rest = new RestTemplate();
//    
//    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
//    
//    public AiResponse callAgent(MessageRequest msgRequest) {
//        var req = new HashMap<String, Object>();
//        req.put("message", msgRequest.getContent());
//        req.put("user_id", String.valueOf(msgRequest.getUserId()));
//
//        if (msgRequest.getTools() != null) {
//            req.put("preferred_tool", msgRequest.getTools());
//        }
//        
//        if(msgRequest.getSessionId() == null) {
//            throw new RuntimeException("Session Id is null");
//        }
//        
//        req.put("session_id", msgRequest.getSessionId());
//        
//        try {
//            var response = rest.postForEntity(aiUrl, req, AiResponse.class);
//            return response.getBody();
//        } catch (HttpClientErrorException | HttpServerErrorException e) {
//            // Handle 4xx and 5xx errors
//            AiResponse errorResponse = new AiResponse();
//            errorResponse.setContent("Error");
//            errorResponse.setResponse("Error");
//            errorResponse.setError(true);  // Set error flag
//            errorResponse.setToolUsed(List.of("AI"));
//            return errorResponse;
//        } catch (Exception e) {
//            // Handle other exceptions (network issues, etc.)
//            AiResponse errorResponse = new AiResponse();
//            errorResponse.setContent("Error");
//            errorResponse.setResponse("Error");
//            errorResponse.setError(true);  // Set error flag
//            errorResponse.setToolUsed(List.of("AI"));
//            return errorResponse;
//        }
//    }
//
//    public AiResponse continueAgent(ContinueRequest continueRequest) {
//        try {
//            // Prepare the request payload for the Python FastAPI backend
//            Map<String, Object> requestPayload = new HashMap<>();
//            requestPayload.put("run_id", continueRequest.getRunId());
//            requestPayload.put("user_inputs", continueRequest.getUserInputs());
//            requestPayload.put("session_id", continueRequest.getSessionId());
//            requestPayload.put("user_id", continueRequest.getUserId());
//            
//            // Set headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            
//            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);
//            
//            // Call the Python backend's /continue endpoint
//            ResponseEntity<AiResponse> response = rest.postForEntity(
//                aiUrl + "/continue", 
//                requestEntity, 
//                AiResponse.class
//            );
//            
//            // Return the response from Python backend
//            AiResponse aiResponse = response.getBody();
//            
//            if (aiResponse != null && !aiResponse.isError()) {
//                log.info("Successfully continued agent run. RunId: {}, Session: {}", 
//                    continueRequest.getRunId(), continueRequest.getSessionId());
//            } else {
//                log.warn("Continue agent returned error or null response. RunId: {}", 
//                    continueRequest.getRunId());
//            }
//            
//            return aiResponse;
//            
//        } catch (HttpClientErrorException e) {
//            log.error("HTTP error continuing agent run: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
//            
//            // Return error response
//            AiResponse errorResponse = new AiResponse();
//            errorResponse.setToolUsed(List.of("Error"));
//            errorResponse.setResponse("Failed to continue execution: HTTP " + e.getStatusCode());
//            errorResponse.setContent("HTTP error: " + e.getResponseBodyAsString());
//            errorResponse.setError(true);
//            errorResponse.setPaused(false);
//            
//            return errorResponse;
//            
//        } catch (Exception e) {
//            log.error("Error continuing agent run: {}", e.getMessage(), e);
//            
//            // Return error response
//            AiResponse errorResponse = new AiResponse();
//            errorResponse.setToolUsed(List.of("Error"));
//            errorResponse.setResponse("Failed to continue execution: " + e.getMessage());
//            errorResponse.setContent("Execution continuation failed");
//            errorResponse.setError(true);
//            errorResponse.setPaused(false);
//            
//            return errorResponse;
//        }
//    }
//
//
//
//}
//




package com.assistant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import com.assistant.dto.AiResponse;
import com.assistant.dto.ContinueRequest;
import com.assistant.dto.MessageRequest;

@Service
public class AiService {
    
    @Value("${python.ai.url:http://localhost:8001}")  // ✅ Fixed - just base URL
    private String aiUrl;

    private final RestTemplate rest = new RestTemplate();
    
    private static final Logger log = LoggerFactory.getLogger(AiService.class);  // ✅ Fixed logger class
    
    public AiResponse callAgent(MessageRequest msgRequest) {
        try {
            // Prepare request payload with correct field names for Python backend
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("message", msgRequest.getContent());
            requestPayload.put("user_id", String.valueOf(msgRequest.getUserId()));
            requestPayload.put("session_id", msgRequest.getSessionId());
            
            if (msgRequest.getTools() != null && !msgRequest.getTools().isEmpty()) {
                requestPayload.put("preferred_tool", msgRequest.getTools());  // ✅ Correct field name
            }
            
            if (msgRequest.getSessionId() == null) {
                throw new RuntimeException("Session Id is null");
            }
            
            // Set proper headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);
            
            log.debug("Calling AI backend at: {}/assist", aiUrl);
            log.debug("Request payload: {}", requestPayload);
            
            // Call the /assist endpoint
            ResponseEntity<AiResponse> response = rest.postForEntity(
                aiUrl + "/assist",  // ✅ Explicitly add /assist endpoint
                requestEntity, 
                AiResponse.class
            );
            
            AiResponse aiResponse = response.getBody();
            
            if (aiResponse != null) {
                log.info("AI response received. Tools used: {}, Paused: {}, Error: {}", 
                    aiResponse.getToolUsed(), aiResponse.isPaused(), aiResponse.isError());
            }
            
            return aiResponse;
            
        } catch (ResourceAccessException e) {
            log.error("Connection refused - is Python FastAPI running? Error: {}", e.getMessage());
            return createErrorResponse("Cannot connect to AI backend. Is the Python service running?");
            
        } catch (HttpClientErrorException e) {
            log.error("HTTP {} error from AI backend: {}", e.getStatusCode(), e.getResponseBodyAsString());
            
            // Log the specific 422 error details for debugging
            if (e.getStatusCode().value() == 422) {
                log.error("Validation error (422) - check request format: {}", e.getResponseBodyAsString());
            }
            
            return createErrorResponse("AI backend validation error: " + e.getResponseBodyAsString());
            
        } catch (HttpServerErrorException e) {
            log.error("Server error from AI backend: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return createErrorResponse("AI backend server error: " + e.getResponseBodyAsString());
            
        } catch (Exception e) {
            log.error("Unexpected error calling AI agent: {}", e.getMessage(), e);
            return createErrorResponse("Unexpected error: " + e.getMessage());
        }
    }

    public AiResponse continueAgent(ContinueRequest continueRequest) {
        try {
            // Prepare the request payload for the Python FastAPI backend
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("run_id", continueRequest.getRunId());
            requestPayload.put("user_inputs", continueRequest.getUserInputs());
            requestPayload.put("session_id", continueRequest.getSessionId());
            requestPayload.put("user_id", String.valueOf(continueRequest.getUserId()));
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);
            
            log.debug("Continuing agent run. URL: {}/continue", aiUrl);
            log.debug("Continue payload: {}", requestPayload);
            
            // Call the Python backend's /continue endpoint
            ResponseEntity<AiResponse> response = rest.postForEntity(
                aiUrl + "/continue", 
                requestEntity, 
                AiResponse.class
            );
            
            // Return the response from Python backend
            AiResponse aiResponse = response.getBody();
            
            if (aiResponse != null && !aiResponse.isError()) {
                log.info("Successfully continued agent run. RunId: {}, Session: {}", 
                    continueRequest.getRunId(), continueRequest.getSessionId());
            } else {
                log.warn("Continue agent returned error or null response. RunId: {}", 
                    continueRequest.getRunId());
            }
            
            return aiResponse;
            
        } catch (HttpClientErrorException e) {
            log.error("HTTP error continuing agent run: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return createErrorResponse("Failed to continue execution: HTTP " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            
        } catch (Exception e) {
            log.error("Error continuing agent run: {}", e.getMessage(), e);
            return createErrorResponse("Failed to continue execution: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to create consistent error responses
     */
    private AiResponse createErrorResponse(String errorMessage) {
        AiResponse errorResponse = new AiResponse();
        errorResponse.setToolUsed(List.of("Error"));  // ✅ Consistent field name
        errorResponse.setResponse(errorMessage);
        errorResponse.setContent(errorMessage);
        errorResponse.setError(true);
        errorResponse.setPaused(false);
        errorResponse.setHitlAvailable(false);
        errorResponse.setNeedsConfirmation(false);
        
        return errorResponse;
    }
}
