package com.finalshell.command;

import java.util.*;

/**
 * 快捷命令分组
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class QuickCmdGroup {
    
    private String id;
    private String name;
    private int order;
    private List<QuickCmd> commands;
    
    public QuickCmdGroup() {
        this.id = java.util.UUID.randomUUID().toString();
        this.commands = new ArrayList<>();
    }
    
    public QuickCmdGroup(String name) {
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
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public List<QuickCmd> getCommands() {
        return commands;
    }
    
    public void setCommands(List<QuickCmd> commands) {
        this.commands = commands;
    }
    
    public void addCommand(QuickCmd cmd) {
        commands.add(cmd);
        cmd.setGroupId(this.id);
    }
    
    public void removeCommand(QuickCmd cmd) {
        commands.remove(cmd);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
