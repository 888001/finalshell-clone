package com.finalshell.util;

import java.io.File;
import java.io.FileFilter;

/**
 * 连接文件过滤器
 * 用于过滤连接配置文件
 */
public class ConnFileFilter implements FileFilter {
    
    private String extension;
    private boolean includeDirectories;
    
    public ConnFileFilter() {
        this(".json");
    }
    
    public ConnFileFilter(String extension) {
        this(extension, false);
    }
    
    public ConnFileFilter(String extension, boolean includeDirectories) {
        this.extension = extension;
        this.includeDirectories = includeDirectories;
    }
    
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return includeDirectories;
        }
        
        String name = file.getName();
        if (extension != null && !extension.isEmpty()) {
            return name.endsWith(extension);
        }
        
        return true;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    public boolean isIncludeDirectories() {
        return includeDirectories;
    }
    
    public void setIncludeDirectories(boolean includeDirectories) {
        this.includeDirectories = includeDirectories;
    }
    
    public static ConnFileFilter jsonFilter() {
        return new ConnFileFilter(".json");
    }
    
    public static ConnFileFilter xmlFilter() {
        return new ConnFileFilter(".xml");
    }
    
    public static ConnFileFilter allFilesFilter() {
        return new ConnFileFilter(null);
    }
}
