package com.assistant.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assistant.model.Session;
import com.assistant.model.Project;
import com.assistant.model.User;
import com.assistant.repository.ProjectRepository;
import com.assistant.repository.SessionRepository;
import com.assistant.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SessionService {

	@Autowired
    private SessionRepository sessionRepository;
	
	@Autowired
    private UserRepository userRepository;
    private final ProjectRepository projectRepository = null;
    
    @Autowired
    private UserService userService;

    public String createSession(String jwt) {
    	String session_id = UUID.randomUUID().toString();
    	User user = userService.findUserByJwt(jwt);
    	
    	Session session = new Session();
    	session.setUser(user);
    	session.setId(session_id);
    	
    	sessionRepository.save(session);
    	
        return session_id;
    }

//    public List<Session> getSessionsByProjectId(Long projectId) {
//    	Optional<Project> projectOpt = projectRepository.findById(projectId);
//    	Project project = projectOpt.get();
//    	
//        return sessionRepository.findByProject(project);
//    }

    public Session getSessionById(String id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + id));
    }

    public void deleteSession(String id) {
        if (!sessionRepository.existsById(id)) {
            throw new EntityNotFoundException("Session not found with ID: " + id);
        }
        sessionRepository.deleteById(id);
    }

	public List<Session> getSessionsByUserId(Long userId) {
		Optional<User> user = userRepository.findById(userId);
		
		return sessionRepository.findByUser(user.get());
	}
}
