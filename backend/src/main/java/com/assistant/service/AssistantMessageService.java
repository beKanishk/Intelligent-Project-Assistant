package com.assistant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.assistant.model.AssistantMessage;
import com.assistant.model.AssistantSession;
import com.assistant.model.Plugin;
import com.assistant.repository.AssistantMessageRepository;
import com.assistant.repository.AssistantSessionRepository;
import com.assistant.repository.PluginRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AssistantMessageService {

    private final AssistantMessageRepository assistantMessageRepository = null;
    private final AssistantSessionRepository assistantSessionRepository = null;
    
    @Autowired
    private PluginRepository pluginRepository;

//    public AssistantMessage createMessage(AssistantMessage message) {
//        Long sessionId = message.getSession().getId();
//        AssistantSession session = assistantSessionRepository.findById(sessionId)
//                .orElseThrow(() -> new EntityNotFoundException("AssistantSession not found with ID: " + sessionId));
//
//        message.setSession(session);
//        return assistantMessageRepository.save(message);
//    }

    public List<AssistantMessage> getMessagesBySessionId(Long sessionId) {
    	Optional<AssistantSession> session = assistantSessionRepository.findById(sessionId);
        return assistantMessageRepository.findBySession(session.get());
    }

    public void deleteMessage(Long id) {
        if (!assistantMessageRepository.existsById(id)) {
            throw new EntityNotFoundException("AssistantMessage not found with ID: " + id);
        }
        assistantMessageRepository.deleteById(id);
    }

	public AssistantMessage getMessageById(Long id) {
		return assistantMessageRepository.getReferenceById(id);
	}
	
	public AssistantMessage createMessage(AssistantMessage message) {
	    String aiUrl = "http://localhost:8001/ai/assist";
	    
	    RestTemplate restTemplate = new RestTemplate();
	    Map<String, Object> reqBody = new HashMap<>();
	    reqBody.put("message", message.getContent());
	    
	    if (message.getPlugin() != null) {
	        reqBody.put("preferred_tool", message.getPlugin().getName());
	    }

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(reqBody, headers);

	    ResponseEntity<Map> response = restTemplate.postForEntity(aiUrl, entity, Map.class);
	    Map<String, String> result = response.getBody();

	    message.setContent(result.get("response"));
	    
	    // Set plugin (if auto selected by AI)
	    if (message.getPlugin() == null) {
	        Plugin plugin = pluginRepository.findByName(result.get("tool_used"));
	        message.setPlugin(plugin);
	    }

	    return assistantMessageRepository.save(message);
	}

}