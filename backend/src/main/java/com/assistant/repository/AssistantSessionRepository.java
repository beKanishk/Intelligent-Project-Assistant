package com.assistant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assistant.model.AssistantSession;
import com.assistant.model.Project;
import com.assistant.model.User;

public interface AssistantSessionRepository extends JpaRepository<AssistantSession, Long>{
	List<AssistantSession> findByProject(Project project);
	List<AssistantSession> findByUser(User user);
}
