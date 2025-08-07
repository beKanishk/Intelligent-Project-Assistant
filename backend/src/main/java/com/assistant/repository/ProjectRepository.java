package com.assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assistant.model.Project;
import com.assistant.model.User;

import java.util.List;


public interface ProjectRepository extends JpaRepository<Project, Long>{
	List<Project> findByOwner(User owner);
}
