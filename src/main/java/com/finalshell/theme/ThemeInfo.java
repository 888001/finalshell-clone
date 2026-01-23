package com.finalshell.theme;

/**
 * 主题信息
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ThemeInfo {
    
    private String id;
    private String name;
    private String author;
    private String description;
    private String path;
    private boolean builtin;
    
    public ThemeInfo() {
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    public ThemeInfo(String name) {
        this();
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public boolean isBuiltin() {
        return builtin;
    }
    
    public void setBuiltin(boolean builtin) {
        this.builtin = builtin;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
