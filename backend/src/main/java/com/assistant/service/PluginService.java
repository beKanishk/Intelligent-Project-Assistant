package com.assistant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assistant.model.Plugin;
import com.assistant.model.Project;
import com.assistant.repository.PluginRepository;
import com.assistant.repository.ProjectRepository;

@Service
public class PluginService {
    @Autowired
    private PluginRepository pluginRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    public Plugin createPlugin(Plugin plugin) {
        return pluginRepository.save(plugin);
    }

    public List<Plugin> getAllPlugins() {
        return pluginRepository.findAll();
    }

    public Plugin getPluginByName(Long projectId, String name) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));

        List<Plugin> plugins = pluginRepository.findByProject(project); // Pass Project object ✅

        return plugins.stream()
                .filter(plugin -> plugin.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Plugin with name '" + name + "' not found in project " + projectId));
    }

	public Plugin getPluginById(Long id) {
		// TODO Auto-generated method stub
		return pluginRepository.getReferenceById(id);
	}

	public void deletePlugin(Long id) {
		pluginRepository.deleteById(id);
	}


}
