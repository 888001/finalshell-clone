package com.finalshell.process;

/**
 * Task Row - Represents a process/task entry
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TaskRow {
    
    private int pid;
    private String name;
    private double cpuUsage;
    private long memoryUsage;
    private String status;
    private String user;
    private String command;
    
    public TaskRow() {
    }
    
    public TaskRow(int pid, String name) {
        this.pid = pid;
        this.name = name;
    }
    
    public int getPid() {
        return pid;
    }
    
    public void setPid(int pid) {
        this.pid = pid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getCpuUsage() {
        return cpuUsage;
    }
    
    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    
    public long getMemoryUsage() {
        return memoryUsage;
    }
    
    public void setMemoryUsage(long memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    @Override
    public String toString() {
        return name + " (" + pid + ")";
    }
}
