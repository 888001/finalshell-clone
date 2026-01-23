package com.finalshell.monitor;

/**
 * Top命令行数据
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TopRow {
    
    private String uptime;
    private int users;
    private double loadAvg1;
    private double loadAvg5;
    private double loadAvg15;
    
    private int totalTasks;
    private int runningTasks;
    private int sleepingTasks;
    private int stoppedTasks;
    private int zombieTasks;
    
    private double cpuUser;
    private double cpuSystem;
    private double cpuNice;
    private double cpuIdle;
    private double cpuWait;
    private double cpuHi;
    private double cpuSi;
    private double cpuSt;
    
    private long memTotal;
    private long memFree;
    private long memUsed;
    private long memBuffCache;
    
    private long swapTotal;
    private long swapFree;
    private long swapUsed;
    private long swapAvail;
    
    public TopRow() {
    }
    
    public String getUptime() {
        return uptime;
    }
    
    public void setUptime(String uptime) {
        this.uptime = uptime;
    }
    
    public int getUsers() {
        return users;
    }
    
    public void setUsers(int users) {
        this.users = users;
    }
    
    public double getLoadAvg1() {
        return loadAvg1;
    }
    
    public void setLoadAvg1(double loadAvg1) {
        this.loadAvg1 = loadAvg1;
    }
    
    public double getLoadAvg5() {
        return loadAvg5;
    }
    
    public void setLoadAvg5(double loadAvg5) {
        this.loadAvg5 = loadAvg5;
    }
    
    public double getLoadAvg15() {
        return loadAvg15;
    }
    
    public void setLoadAvg15(double loadAvg15) {
        this.loadAvg15 = loadAvg15;
    }
    
    public int getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public int getRunningTasks() {
        return runningTasks;
    }
    
    public void setRunningTasks(int runningTasks) {
        this.runningTasks = runningTasks;
    }
    
    public int getSleepingTasks() {
        return sleepingTasks;
    }
    
    public void setSleepingTasks(int sleepingTasks) {
        this.sleepingTasks = sleepingTasks;
    }
    
    public int getStoppedTasks() {
        return stoppedTasks;
    }
    
    public void setStoppedTasks(int stoppedTasks) {
        this.stoppedTasks = stoppedTasks;
    }
    
    public int getZombieTasks() {
        return zombieTasks;
    }
    
    public void setZombieTasks(int zombieTasks) {
        this.zombieTasks = zombieTasks;
    }
    
    public double getCpuUser() {
        return cpuUser;
    }
    
    public void setCpuUser(double cpuUser) {
        this.cpuUser = cpuUser;
    }
    
    public double getCpuSystem() {
        return cpuSystem;
    }
    
    public void setCpuSystem(double cpuSystem) {
        this.cpuSystem = cpuSystem;
    }
    
    public double getCpuNice() {
        return cpuNice;
    }
    
    public void setCpuNice(double cpuNice) {
        this.cpuNice = cpuNice;
    }
    
    public double getCpuIdle() {
        return cpuIdle;
    }
    
    public void setCpuIdle(double cpuIdle) {
        this.cpuIdle = cpuIdle;
    }
    
    public double getCpuWait() {
        return cpuWait;
    }
    
    public void setCpuWait(double cpuWait) {
        this.cpuWait = cpuWait;
    }
    
    public double getCpuHi() {
        return cpuHi;
    }
    
    public void setCpuHi(double cpuHi) {
        this.cpuHi = cpuHi;
    }
    
    public double getCpuSi() {
        return cpuSi;
    }
    
    public void setCpuSi(double cpuSi) {
        this.cpuSi = cpuSi;
    }
    
    public double getCpuSt() {
        return cpuSt;
    }
    
    public void setCpuSt(double cpuSt) {
        this.cpuSt = cpuSt;
    }
    
    public long getMemTotal() {
        return memTotal;
    }
    
    public void setMemTotal(long memTotal) {
        this.memTotal = memTotal;
    }
    
    public long getMemFree() {
        return memFree;
    }
    
    public void setMemFree(long memFree) {
        this.memFree = memFree;
    }
    
    public long getMemUsed() {
        return memUsed;
    }
    
    public void setMemUsed(long memUsed) {
        this.memUsed = memUsed;
    }
    
    public long getMemBuffCache() {
        return memBuffCache;
    }
    
    public void setMemBuffCache(long memBuffCache) {
        this.memBuffCache = memBuffCache;
    }
    
    public long getSwapTotal() {
        return swapTotal;
    }
    
    public void setSwapTotal(long swapTotal) {
        this.swapTotal = swapTotal;
    }
    
    public long getSwapFree() {
        return swapFree;
    }
    
    public void setSwapFree(long swapFree) {
        this.swapFree = swapFree;
    }
    
    public long getSwapUsed() {
        return swapUsed;
    }
    
    public void setSwapUsed(long swapUsed) {
        this.swapUsed = swapUsed;
    }
    
    public long getSwapAvail() {
        return swapAvail;
    }
    
    public void setSwapAvail(long swapAvail) {
        this.swapAvail = swapAvail;
    }
    
    public double getCpuUsagePercent() {
        return 100 - cpuIdle;
    }
    
    public double getMemUsagePercent() {
        if (memTotal == 0) {
            return 0;
        }
        return (memUsed * 100.0) / memTotal;
    }
}
