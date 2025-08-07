package com.assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assistant.model.Plugin;
import com.assistant.model.Project;

import java.util.List;


public interface PluginRepository extends JpaRepository<Plugin, Long>{
	Plugin findByName(String name);

	List<Plugin> findByProject(Project project);
}
