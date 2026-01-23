package com.finalshell.monitor;

/**
 * 进程任务信息
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TaskInfo {
    
    private int pid;
    private String user;
    private String command;
    private double cpuPercent;
    private double memPercent;
    private long vsz;
    private long rss;
    private String stat;
    private String time;
    private int priority;
    private int nice;
    
    public TaskInfo() {
    }
    
    public TaskInfo(int pid, String user, String command) {
        this.pid = pid;
        this.user = user;
        this.command = command;
    }
    
    public int getPid() {
        return pid;
    }
    
    public void setPid(int pid) {
        this.pid = pid;
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
    
    public double getCpuPercent() {
        return cpuPercent;
    }
    
    public void setCpuPercent(double cpuPercent) {
        this.cpuPercent = cpuPercent;
    }
    
    public double getMemPercent() {
        return memPercent;
    }
    
    public void setMemPercent(double memPercent) {
        this.memPercent = memPercent;
    }
    
    public long getVsz() {
        return vsz;
    }
    
    public void setVsz(long vsz) {
        this.vsz = vsz;
    }
    
    public long getRss() {
        return rss;
    }
    
    public void setRss(long rss) {
        this.rss = rss;
    }
    
    public String getStat() {
        return stat;
    }
    
    public void setStat(String stat) {
        this.stat = stat;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public int getNice() {
        return nice;
    }
    
    public void setNice(int nice) {
        this.nice = nice;
    }
    
    @Override
    public String toString() {
        return String.format("PID=%d, User=%s, CPU=%.1f%%, MEM=%.1f%%, CMD=%s", 
            pid, user, cpuPercent, memPercent, command);
    }
}
