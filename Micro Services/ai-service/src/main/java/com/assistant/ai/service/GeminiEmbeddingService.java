package com.assistant.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiEmbeddingService {

    @Value("${google.api.key}")
    private final String API_KEY = "YOUR_GOOGLE_API_KEY";

    private final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent";

    public List<Float> getEmbedding(String text) {
        RestTemplate restTemplate = new RestTemplate();

        // Request body structure for Gemini API
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "models/gemini-embedding-001");

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(Collections.singletonMap("text", text)));
        requestBody.put("content", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", API_KEY);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Call API
        ResponseEntity<Map> response = restTemplate.exchange(
                ENDPOINT,
                HttpMethod.POST,
                entity,
                Map.class
        );

        // Extract embedding
        Map respBody = response.getBody();
        if (respBody == null || !respBody.containsKey("embedding")) {
            throw new RuntimeException("No embedding returned from Gemini API: " + respBody);
        }

        Map embeddingMap = (Map) respBody.get("embedding");
        List<Double> values = (List<Double>) embeddingMap.get("values");

        // Convert Double -> Float
        List<Float> embedding = new ArrayList<>();
        for (Double d : values) {
            embedding.add(d.floatValue());
        }

        return embedding;
    }
}

