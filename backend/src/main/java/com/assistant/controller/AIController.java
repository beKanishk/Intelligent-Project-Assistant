package com.assistant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assistant.service.AiService;
import com.assistant.service.AssistantMessageService;

@RestController
@RequestMapping("/api")
public class AIController {
	
	@Autowired
	private AiService aiService;
	
	@Autowired
	private AssistantMessageService assistantMessageService;
	
	@PostMapping("/assistant/message")
	public ResponseEntity<MessageDto> handleMessage(@RequestBody MessageDto dto) {
	    var aiResp = aiService.callAgent(dto.getContent(), dto.getPreferredTool());

	    dto.setPluginUsed(aiResp.tool_used);
	    dto.setAiResponse(aiResp.response);

//	    var entity = assistantMessageService.createMessage();
	    
	    
	    return ResponseEntity.ok(dto);
	}

}
