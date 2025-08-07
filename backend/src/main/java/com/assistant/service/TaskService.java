package com.assistant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assistant.model.Project;
import com.assistant.model.Task;
import com.assistant.repository.ProjectRepository;
import com.assistant.repository.TaskRepository;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getTasksByProject(Project project) {
        return taskRepository.findByProject(project);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

	public List<Task> getTasksByProjectId(Long projectId) {
		Optional<Project> project = projectRepository.findById(projectId);
		return taskRepository.findByProject(project.get());
	}

	public Task getTaskById(Long id) {
		Optional<Task> taskOpt = taskRepository.findById(id);
		return taskOpt.get();
	}
}

