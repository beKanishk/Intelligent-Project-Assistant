package com.assistant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.assistant.model.Plugin;
import com.assistant.model.PluginExecution;
import com.assistant.repository.PluginExecutionRepository;

@Service
public class PluginExecutionService {
    @Autowired
    private PluginExecutionRepository pluginExecutionRepository;

    public PluginExecution logExecution(PluginExecution execution) {
        return pluginExecutionRepository.save(execution);
    }

    public List<PluginExecution> getExecutionsByPlugin(Plugin plugin) {
        return pluginExecutionRepository.findByPlugin(plugin);
    }

	public PluginExecution createPluginExecution(PluginExecution execution) {
		
		return pluginExecutionRepository.save(execution);
	}

	public List<PluginExecution> getExecutionsBySessionId(Long sessionId) {
		return pluginExecutionRepository.findBySessionId(sessionId);
	}
	
	public PluginExecution getPluginExecutionById(Long id) {
	    return pluginExecutionRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("PluginExecution not found with id " + id));
	}

	public void deletePluginExecution(Long id) {
	    pluginExecutionRepository.deleteById(id);
	}
	
	public String callPythonAI(Map<String, Object> inputData, String pluginName, Long sessionId, Long taskId) {
	    RestTemplate restTemplate = new RestTemplate();

	    Map<String, Object> requestBody = new HashMap<>();
	    requestBody.put("sessionId", sessionId);
	    requestBody.put("taskId", taskId);
	    requestBody.put("pluginName", pluginName);
	    requestBody.put("inputData", inputData);

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

	    ResponseEntity<String> response = restTemplate.postForEntity(
	        "http://localhost:8001/execute", request, String.class
	    );

	    return response.getBody();
	}
	
	

}

