package com.assistant.dto;

import java.util.List;

public class AiResponse {
	private List<String> tool_used;
    private String response;
    private String content;
    
	public List<String> getTool_used() {
		return tool_used;
	}
	public void setTool_used(List<String> tool_used) {
		this.tool_used = tool_used;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
    
    
}
