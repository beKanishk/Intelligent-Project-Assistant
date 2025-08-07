package com.assistant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assistant.model.AssistantMessage;
import com.assistant.model.AssistantSession;
import com.assistant.model.Project;

public interface AssistantMessageRepository extends JpaRepository<AssistantMessage, Long>{

	List<AssistantMessage> findByProject(Project project);
	
	List<AssistantMessage> findByContent(String content);
	
	List<AssistantMessage> findBySession(AssistantSession session);
}
