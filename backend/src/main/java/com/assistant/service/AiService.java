package com.assistant.service;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.assistant.dto.AiResponse;
import com.assistant.dto.MessageRequest;

@Service
public class AiService {
    @Value("${python.ai.url:http://localhost:8001/assist}")
    private String aiUrl;

    private final RestTemplate rest = new RestTemplate();

//    public AiResponse callAgent(String message, String preferredTool) {
//        var req = new HashMap<String, Object>();
//        req.put("message", message);
//
//        if (preferredTool != null) {
//            req.put("preferred_tool", preferredTool); // ✅ Only include if non-null
//        }
//
//        var response = rest.postForEntity(aiUrl, req, AiResponse.class);
//        return response.getBody();
//    }

    public AiResponse callAgent(MessageRequest msgRequest) {
        var req = new HashMap<String, Object>();
        req.put("message", msgRequest.getContent());

        if (msgRequest.getTools() != null) {
            req.put("preferred_tool", msgRequest.getTools()); // ✅ Only include if non-null
        }
        
        if(msgRequest.getSessionId() == null) {
        	String session_id = UUID.randomUUID().toString();
        	req.put("session_id", session_id);
        }
        else {
        	req.put("session_id", msgRequest.getSessionId());
        }
        
        
        var response = rest.postForEntity(aiUrl, req, AiResponse.class);
        return response.getBody();
    }

}

