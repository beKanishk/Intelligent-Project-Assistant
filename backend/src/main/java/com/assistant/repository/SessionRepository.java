package com.assistant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assistant.model.Project;
import com.assistant.model.Session;
import com.assistant.model.User;

public interface SessionRepository extends JpaRepository<Session, Long>{
	List<Session> findByProject(Project project);
	List<Session> findByUser(User user);
}
