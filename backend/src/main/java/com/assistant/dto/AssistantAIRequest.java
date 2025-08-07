package com.assistant.dto;

import java.util.Map;

public class AssistantAIRequest {
    private Long sessionId;
    private Long taskId;
    private String pluginName;
    private Map<String, Object> inputData;

    // Getters and Setters
    public Long getSessionId() {
        return sessionId;
    }
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getTaskId() {
        return taskId;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getPluginName() {
        return pluginName;
    }
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public Map<String, Object> getInputData() {
        return inputData;
    }
    public void setInputData(Map<String, Object> inputData) {
        this.inputData = inputData;
    }
}
