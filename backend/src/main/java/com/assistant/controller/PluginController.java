package com.assistant.controller;

import com.assistant.model.Plugin;
import com.assistant.service.PluginService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assistant/plugins")
@PreAuthorize("isAuthenticated()")
public class PluginController {

    private final PluginService pluginService;

    public PluginController(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @PostMapping
    public Plugin createPlugin(@RequestBody Plugin plugin) {
        return pluginService.createPlugin(plugin);
    }

    @GetMapping
    public List<Plugin> getAllPlugins() {
        return pluginService.getAllPlugins();
    }

    @GetMapping("/{id}")
    public Plugin getPluginById(@PathVariable Long id) {
        return pluginService.getPluginById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePlugin(@PathVariable Long id) {
        pluginService.deletePlugin(id);
    }
}
