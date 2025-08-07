package com.assistant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assistant.model.Project;
import com.assistant.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
	List<Task> findByProject(Project project);
}
