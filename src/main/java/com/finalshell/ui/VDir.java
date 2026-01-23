package com.finalshell.ui;

import java.io.*;
import java.util.*;

/**
 * 虚拟目录
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - VDir
 */
public class VDir extends VFile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<VFile> children = new ArrayList<>();
    private boolean loaded = false;
    
    public VDir() {
        setType(TYPE_DIRECTORY);
    }
    
    public VDir(String name) {
        setName(name);
        setType(TYPE_DIRECTORY);
    }
    
    public VDir(File file) {
        super(file);
        setType(TYPE_DIRECTORY);
    }
    
    public void addChild(VFile file) {
        children.add(file);
        file.setParentDirId(getFileId());
    }
    
    public void removeChild(VFile file) {
        children.remove(file);
    }
    
    public List<VFile> getChildren() {
        return children;
    }
    
    public void setChildren(List<VFile> children) {
        this.children = children;
    }
    
    public List<VFile> getFiles() {
        List<VFile> files = new ArrayList<>();
        for (VFile child : children) {
            if (child.isFile()) {
                files.add(child);
            }
        }
        return files;
    }
    
    public List<VDir> getSubDirs() {
        List<VDir> dirs = new ArrayList<>();
        for (VFile child : children) {
            if (child.isDirectory() && child instanceof VDir) {
                dirs.add((VDir) child);
            }
        }
        return dirs;
    }
    
    public VFile findChild(String name) {
        for (VFile child : children) {
            if (name.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }
    
    public void clear() {
        children.clear();
    }
    
    public int getChildCount() {
        return children.size();
    }
    
    public boolean isLoaded() { return loaded; }
    public void setLoaded(boolean loaded) { this.loaded = loaded; }
    
    public void sortChildren(Comparator<VFile> comparator) {
        children.sort(comparator);
    }
    
    public void sortByName() {
        children.sort((a, b) -> {
            if (a.isDirectory() != b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });
    }
}
