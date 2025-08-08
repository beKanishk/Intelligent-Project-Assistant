package com.assistant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.assistant.model.AssistantSession;
import com.assistant.model.Project;
import com.assistant.model.User;
import com.assistant.repository.AssistantSessionRepository;
import com.assistant.repository.ProjectRepository;
import com.assistant.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AssistantSessionService {

    private final AssistantSessionRepository assistantSessionRepository = null;
    private final UserRepository userRepository = null;
    private final ProjectRepository projectRepository = null;

//    public AssistantSession createSession(AssistantSession session) {
//        Long userId = session.getUser().getId();
//        Long projectId = session.getProject().getId();
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));
//
//        session.setUser(user);
//        session.setProject(project);
//        return assistantSessionRepository.save(session);
//    }

    public List<AssistantSession> getSessionsByProjectId(Long projectId) {
    	Optional<Project> projectOpt = projectRepository.findById(projectId);
    	Project project = projectOpt.get();
    	
        return assistantSessionRepository.findByProject(project);
    }

    public AssistantSession getSessionById(Long id) {
        return assistantSessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + id));
    }

    public void deleteSession(Long id) {
        if (!assistantSessionRepository.existsById(id)) {
            throw new EntityNotFoundException("Session not found with ID: " + id);
        }
        assistantSessionRepository.deleteById(id);
    }

	public List<AssistantSession> getSessionsByUserId(Long userId) {
		Optional<User> user = userRepository.findById(userId);
		
		return assistantSessionRepository.findByUser(user.get());
	}
}
