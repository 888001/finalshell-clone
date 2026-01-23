package com.finalshell.monitor;

/**
 * 命令包装类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CommandWrap {
    
    private String command;
    private String description;
    private int interval;
    private boolean enabled;
    
    public CommandWrap() {
    }
    
    public CommandWrap(String command, String description) {
        this.command = command;
        this.description = description;
        this.interval = 1000;
        this.enabled = true;
    }
    
    public CommandWrap(String command, String description, int interval) {
        this.command = command;
        this.description = description;
        this.interval = interval;
        this.enabled = true;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getInterval() {
        return interval;
    }
    
    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return description != null ? description : command;
    }
}
