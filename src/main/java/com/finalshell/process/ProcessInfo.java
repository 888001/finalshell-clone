package com.finalshell.process;

/**
 * 进程信息
 */
public class ProcessInfo {
    private int pid;
    private String user;
    private double cpuPercent;
    private double memPercent;
    private long vsz;       // 虚拟内存 (KB)
    private long rss;       // 常驻内存 (KB)
    private String tty;
    private String stat;
    private String startTime;
    private String time;
    private String command;
    
    public ProcessInfo() {}
    
    // Getters and Setters
    public int getPid() { return pid; }
    public void setPid(int pid) { this.pid = pid; }
    
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    
    public double getCpuPercent() { return cpuPercent; }
    public void setCpuPercent(double cpuPercent) { this.cpuPercent = cpuPercent; }
    
    public double getMemPercent() { return memPercent; }
    public void setMemPercent(double memPercent) { this.memPercent = memPercent; }
    
    public long getVsz() { return vsz; }
    public void setVsz(long vsz) { this.vsz = vsz; }
    
    public long getRss() { return rss; }
    public void setRss(long rss) { this.rss = rss; }
    
    public String getTty() { return tty; }
    public void setTty(String tty) { this.tty = tty; }
    
    public String getStat() { return stat; }
    public void setStat(String stat) { this.stat = stat; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public String getState() { return stat; }
    public void setState(String state) { this.stat = state; }
    
    public String getMemoryDisplay() {
        if (rss < 1024) return rss + " KB";
        if (rss < 1024 * 1024) return String.format("%.1f MB", rss / 1024.0);
        return String.format("%.2f GB", rss / (1024.0 * 1024));
    }
}
