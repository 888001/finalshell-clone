package com.finalshell.sftp;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * 可执行文件过滤器
 */
public class ExeFileFilter extends FileFilter {
    
    private String[] extensions = {".exe", ".bat", ".cmd", ".sh", ".bin"};
    private String description = "可执行文件";
    
    public ExeFileFilter() {}
    
    public ExeFileFilter(String[] extensions, String description) {
        this.extensions = extensions;
        this.description = description;
    }
    
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        String name = f.getName().toLowerCase();
        for (String ext : extensions) {
            if (name.endsWith(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
