package com.assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assistant.model.Plugin;
import com.assistant.model.PluginExecution;
import java.util.List;


public interface PluginExecutionRepository extends JpaRepository<PluginExecution, Long>{
	List<PluginExecution> findByPlugin(Plugin plugin);
	
	@Query("SELECT pe FROM PluginExecution pe WHERE pe.task.session.id = :sessionId")
	List<PluginExecution> findBySessionId(@Param("sessionId") Long sessionId);

}
