package com.finalshell.plugin;

import java.io.File;

/**
 * 插件信息
 */
public class PluginInfo {
    private String id;
    private String name;
    private String version;
    private String author;
    private String description;
    private String mainClass;
    private File jarFile;
    private boolean enabled;
    private Plugin instance;
    
    public PluginInfo() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getMainClass() { return mainClass; }
    public void setMainClass(String mainClass) { this.mainClass = mainClass; }
    
    public File getJarFile() { return jarFile; }
    public void setJarFile(File jarFile) { this.jarFile = jarFile; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public Plugin getInstance() { return instance; }
    public void setInstance(Plugin instance) { this.instance = instance; }
}
