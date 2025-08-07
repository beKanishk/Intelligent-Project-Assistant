package com.assistant.controller;

import com.assistant.dto.AssistantAIRequest;
import com.assistant.model.Plugin;
import com.assistant.model.PluginExecution;
import com.assistant.service.PluginExecutionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/assistant/plugin-executions")
@PreAuthorize("isAuthenticated()")
public class PluginExecutionController {

    private final PluginExecutionService pluginExecutionService;

    public PluginExecutionController(PluginExecutionService pluginExecutionService) {
        this.pluginExecutionService = pluginExecutionService;
    }

    @PostMapping
    public PluginExecution createExecution(@RequestBody PluginExecution execution) {
        return pluginExecutionService.createPluginExecution(execution);
    }

    @GetMapping("/session/{sessionId}")
    public List<PluginExecution> getExecutionsBySession(@PathVariable Long sessionId) {
        return pluginExecutionService.getExecutionsBySessionId(sessionId);
    }

    @GetMapping("/{id}")
    public PluginExecution getExecutionById(@PathVariable Long id) {
        return pluginExecutionService.getPluginExecutionById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteExecution(@PathVariable Long id) {
        pluginExecutionService.deletePluginExecution(id);
    }
    
   

}
