package com.assistant.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {
    @Value("${python.ai.url:http://localhost:8001/assist}")
    private String aiUrl;

    private final RestTemplate rest = new RestTemplate();

    public AiResponse callAgent(String message, String preferredTool) {
        var req = new HashMap<String, Object>();
        req.put("message", message);

        if (preferredTool != null) {
            req.put("preferred_tool", preferredTool); // ✅ Only include if non-null
        }

        var response = rest.postForEntity(aiUrl, req, AiResponse.class);
        return response.getBody();
    }


    public static class AiResponse {
        public String tool_used;
        public String response;
    }
}

