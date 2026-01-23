package com.finalshell.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Monitor Data - System monitoring data model
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Network_Protocol_Analysis.md - Monitoring Data Protocol
 */
public class MonitorData {
    
    // CPU
    private double cpuUsage;
    private double cpuUser;
    private double cpuSystem;
    private double cpuIdle;
    private int cpuCores;
    private String cpuModel;
    
    // Memory
    private long memTotal;
    private long memUsed;
    private long memFree;
    private long memBuffers;
    private long memCached;
    private double memUsagePercent;
    
    // Swap
    private long swapTotal;
    private long swapUsed;
    private long swapFree;
    
    // Disk
    private List<DiskInfo> disks = new ArrayList<>();
    
    // Network
    private List<NetworkInfo> networks = new ArrayList<>();
    private long netRxBytes;
    private long netTxBytes;
    private long netRxSpeed;
    private long netTxSpeed;
    
    // System
    private String hostname;
    private String osName;
    private String osVersion;
    private String kernelVersion;
    private long uptime;
    private double loadAverage1;
    private double loadAverage5;
    private double loadAverage15;
    
    // Processes
    private int processCount;
    private List<ProcessInfo> topProcesses = new ArrayList<>();
    
    // Timestamp
    private long timestamp;
    
    public MonitorData() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // CPU Getters/Setters
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    
    public double getCpuUser() { return cpuUser; }
    public void setCpuUser(double cpuUser) { this.cpuUser = cpuUser; }
    
    public double getCpuSystem() { return cpuSystem; }
    public void setCpuSystem(double cpuSystem) { this.cpuSystem = cpuSystem; }
    
    public double getCpuIdle() { return cpuIdle; }
    public void setCpuIdle(double cpuIdle) { this.cpuIdle = cpuIdle; }
    
    public int getCpuCores() { return cpuCores; }
    public void setCpuCores(int cpuCores) { this.cpuCores = cpuCores; }
    
    public String getCpuModel() { return cpuModel; }
    public void setCpuModel(String cpuModel) { this.cpuModel = cpuModel; }
    
    // Memory Getters/Setters
    public long getMemTotal() { return memTotal; }
    public void setMemTotal(long memTotal) { this.memTotal = memTotal; }
    
    public long getMemUsed() { return memUsed; }
    public void setMemUsed(long memUsed) { this.memUsed = memUsed; }
    
    public long getMemFree() { return memFree; }
    public void setMemFree(long memFree) { this.memFree = memFree; }
    
    public long getMemBuffers() { return memBuffers; }
    public void setMemBuffers(long memBuffers) { this.memBuffers = memBuffers; }
    
    public long getMemCached() { return memCached; }
    public void setMemCached(long memCached) { this.memCached = memCached; }
    
    public double getMemUsagePercent() { return memUsagePercent; }
    public void setMemUsagePercent(double memUsagePercent) { this.memUsagePercent = memUsagePercent; }
    
    // Swap Getters/Setters
    public long getSwapTotal() { return swapTotal; }
    public void setSwapTotal(long swapTotal) { this.swapTotal = swapTotal; }
    
    public long getSwapUsed() { return swapUsed; }
    public void setSwapUsed(long swapUsed) { this.swapUsed = swapUsed; }
    
    public long getSwapFree() { return swapFree; }
    public void setSwapFree(long swapFree) { this.swapFree = swapFree; }
    
    // Disk Getters/Setters
    public List<DiskInfo> getDisks() { return disks; }
    public void setDisks(List<DiskInfo> disks) { this.disks = disks; }
    public void addDisk(DiskInfo disk) { this.disks.add(disk); }
    
    // Network Getters/Setters
    public List<NetworkInfo> getNetworks() { return networks; }
    public void setNetworks(List<NetworkInfo> networks) { this.networks = networks; }
    public void addNetwork(NetworkInfo network) { this.networks.add(network); }
    
    public long getNetRxBytes() { return netRxBytes; }
    public void setNetRxBytes(long netRxBytes) { this.netRxBytes = netRxBytes; }
    
    public long getNetTxBytes() { return netTxBytes; }
    public void setNetTxBytes(long netTxBytes) { this.netTxBytes = netTxBytes; }
    
    public long getNetRxSpeed() { return netRxSpeed; }
    public void setNetRxSpeed(long netRxSpeed) { this.netRxSpeed = netRxSpeed; }
    
    public long getNetTxSpeed() { return netTxSpeed; }
    public void setNetTxSpeed(long netTxSpeed) { this.netTxSpeed = netTxSpeed; }
    
    // System Getters/Setters
    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }
    
    public String getOsName() { return osName; }
    public void setOsName(String osName) { this.osName = osName; }
    
    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }
    
    public String getKernelVersion() { return kernelVersion; }
    public void setKernelVersion(String kernelVersion) { this.kernelVersion = kernelVersion; }
    
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
    
    public double getLoadAverage1() { return loadAverage1; }
    public void setLoadAverage1(double loadAverage1) { this.loadAverage1 = loadAverage1; }
    
    public double getLoadAverage5() { return loadAverage5; }
    public void setLoadAverage5(double loadAverage5) { this.loadAverage5 = loadAverage5; }
    
    public double getLoadAverage15() { return loadAverage15; }
    public void setLoadAverage15(double loadAverage15) { this.loadAverage15 = loadAverage15; }
    
    // Process Getters/Setters
    public int getProcessCount() { return processCount; }
    public void setProcessCount(int processCount) { this.processCount = processCount; }
    
    public List<ProcessInfo> getTopProcesses() { return topProcesses; }
    public void setTopProcesses(List<ProcessInfo> topProcesses) { this.topProcesses = topProcesses; }
    public void addProcess(ProcessInfo process) { this.topProcesses.add(process); }
    
    // Timestamp
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    /**
     * Format bytes to human readable
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    /**
     * Format uptime to human readable
     */
    public static String formatUptime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        
        if (days > 0) {
            return String.format("%d天 %d小时 %d分钟", days, hours, minutes);
        } else if (hours > 0) {
            return String.format("%d小时 %d分钟", hours, minutes);
        } else {
            return String.format("%d分钟", minutes);
        }
    }
    
    /**
     * Disk Information
     */
    public static class DiskInfo {
        private String name;
        private String mountPoint;
        private String fsType;
        private long total;
        private long used;
        private long free;
        private double usagePercent;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getMountPoint() { return mountPoint; }
        public void setMountPoint(String mountPoint) { this.mountPoint = mountPoint; }
        
        public String getFsType() { return fsType; }
        public void setFsType(String fsType) { this.fsType = fsType; }
        
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        
        public long getUsed() { return used; }
        public void setUsed(long used) { this.used = used; }
        
        public long getFree() { return free; }
        public void setFree(long free) { this.free = free; }
        
        public double getUsagePercent() { return usagePercent; }
        public void setUsagePercent(double usagePercent) { this.usagePercent = usagePercent; }
    }
    
    /**
     * Network Interface Information
     */
    public static class NetworkInfo {
        private String name;
        private String ipAddress;
        private String macAddress;
        private long rxBytes;
        private long txBytes;
        private long rxSpeed;
        private long txSpeed;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getMacAddress() { return macAddress; }
        public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
        
        public long getRxBytes() { return rxBytes; }
        public void setRxBytes(long rxBytes) { this.rxBytes = rxBytes; }
        
        public long getTxBytes() { return txBytes; }
        public void setTxBytes(long txBytes) { this.txBytes = txBytes; }
        
        public long getRxSpeed() { return rxSpeed; }
        public void setRxSpeed(long rxSpeed) { this.rxSpeed = rxSpeed; }
        
        public long getTxSpeed() { return txSpeed; }
        public void setTxSpeed(long txSpeed) { this.txSpeed = txSpeed; }
    }
    
    /**
     * Process Information
     */
    public static class ProcessInfo {
        private int pid;
        private String name;
        private String user;
        private double cpuPercent;
        private double memPercent;
        private long memBytes;
        private String state;
        private String command;
        
        public int getPid() { return pid; }
        public void setPid(int pid) { this.pid = pid; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        
        public double getCpuPercent() { return cpuPercent; }
        public void setCpuPercent(double cpuPercent) { this.cpuPercent = cpuPercent; }
        
        public double getMemPercent() { return memPercent; }
        public void setMemPercent(double memPercent) { this.memPercent = memPercent; }
        
        public long getMemBytes() { return memBytes; }
        public void setMemBytes(long memBytes) { this.memBytes = memBytes; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getCommand() { return command; }
        public void setCommand(String command) { this.command = command; }
    }
}
