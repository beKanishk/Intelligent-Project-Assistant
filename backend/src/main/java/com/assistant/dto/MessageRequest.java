package com.assistant.dto;

import java.util.List;

public class MessageRequest {
	private String content;
	private String sessionId;
	private List<String> tools;
	private Long userId;
	private String role;
	private boolean paused;
	private boolean needsConfirmation;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public List<String> getTools() {
		return tools;
	}
	public void setTools(List<String> tools) {
		this.tools = tools;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isPaused() {
		return paused;
	}
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	public boolean isNeedsConfirmation() {
		return needsConfirmation;
	}
	public void setNeedsConfirmation(boolean needsConfirmation) {
		this.needsConfirmation = needsConfirmation;
	}
	
	
}
