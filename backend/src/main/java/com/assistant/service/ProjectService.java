package com.assistant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assistant.model.Project;
import com.assistant.model.User;
import com.assistant.repository.ProjectRepository;
import com.assistant.repository.UserRepository;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> getProjectsByUser(Long userId) {
    	Optional<User> user = userRepository.findById(userId);
        return projectRepository.findByOwner(user.get());
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
    }

	public void deleteProject(Long id) {
		projectRepository.deleteById(id);
	}
}

