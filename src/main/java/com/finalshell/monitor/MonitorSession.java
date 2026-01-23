package com.finalshell.monitor;

import com.finalshell.ssh.SSHException;
import com.finalshell.ssh.SSHSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

/**
 * Monitor Session - Collects system monitoring data via SSH
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Network_Protocol_Analysis.md - Monitoring Commands
 */
public class MonitorSession {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitorSession.class);
    
    private final SSHSession sshSession;
    private final List<MonitorListener> listeners = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService scheduler;
    
    private volatile boolean running = false;
    private int intervalSeconds = 2;
    
    // Previous values for speed calculation
    private long prevRxBytes = 0;
    private long prevTxBytes = 0;
    private long prevTimestamp = 0;
    
    public MonitorSession(SSHSession sshSession) {
        this.sshSession = sshSession;
    }
    
    /**
     * Start monitoring
     */
    public void start() {
        if (running) return;
        
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                MonitorData data = collectData();
                fireDataReceived(data);
            } catch (Exception e) {
                logger.error("Monitor data collection failed", e);
                fireError(e.getMessage());
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);
        
        logger.info("Monitoring started with interval: {}s", intervalSeconds);
    }
    
    /**
     * Stop monitoring
     */
    public void stop() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
        logger.info("Monitoring stopped");
    }
    
    /**
     * Collect all monitoring data
     */
    private MonitorData collectData() throws SSHException {
        MonitorData data = new MonitorData();
        
        // System info
        collectSystemInfo(data);
        
        // CPU
        collectCpuInfo(data);
        
        // Memory
        collectMemoryInfo(data);
        
        // Disk
        collectDiskInfo(data);
        
        // Network
        collectNetworkInfo(data);
        
        // Processes
        collectProcessInfo(data);
        
        return data;
    }
    
    /**
     * Collect system information
     */
    private void collectSystemInfo(MonitorData data) throws SSHException {
        // Hostname
        String hostname = execCommand("hostname").trim();
        data.setHostname(hostname);
        
        // OS info
        String osInfo = execCommand("cat /etc/os-release 2>/dev/null | grep PRETTY_NAME | cut -d= -f2 | tr -d '\"' || uname -o");
        data.setOsName(osInfo.trim());
        
        // Kernel version
        String kernel = execCommand("uname -r").trim();
        data.setKernelVersion(kernel);
        
        // Uptime
        String uptimeStr = execCommand("cat /proc/uptime | awk '{print $1}'").trim();
        try {
            data.setUptime((long) Double.parseDouble(uptimeStr));
        } catch (Exception e) {
            data.setUptime(0);
        }
        
        // Load average
        String loadAvg = execCommand("cat /proc/loadavg").trim();
        String[] loads = loadAvg.split("\\s+");
        if (loads.length >= 3) {
            try {
                data.setLoadAverage1(Double.parseDouble(loads[0]));
                data.setLoadAverage5(Double.parseDouble(loads[1]));
                data.setLoadAverage15(Double.parseDouble(loads[2]));
            } catch (Exception e) {
                // Ignore parse errors
            }
        }
    }
    
    /**
     * Collect CPU information
     */
    private void collectCpuInfo(MonitorData data) throws SSHException {
        // CPU cores
        String coresStr = execCommand("nproc 2>/dev/null || grep -c processor /proc/cpuinfo").trim();
        try {
            data.setCpuCores(Integer.parseInt(coresStr));
        } catch (Exception e) {
            data.setCpuCores(1);
        }
        
        // CPU model
        String cpuModel = execCommand("grep 'model name' /proc/cpuinfo | head -1 | cut -d: -f2").trim();
        data.setCpuModel(cpuModel);
        
        // CPU usage from /proc/stat
        String cpuStat = execCommand("head -1 /proc/stat").trim();
        // cpu  user nice system idle iowait irq softirq
        Pattern pattern = Pattern.compile("cpu\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");
        Matcher matcher = pattern.matcher(cpuStat);
        if (matcher.find()) {
            long user = Long.parseLong(matcher.group(1));
            long nice = Long.parseLong(matcher.group(2));
            long system = Long.parseLong(matcher.group(3));
            long idle = Long.parseLong(matcher.group(4));
            
            long total = user + nice + system + idle;
            if (total > 0) {
                data.setCpuUser((user + nice) * 100.0 / total);
                data.setCpuSystem(system * 100.0 / total);
                data.setCpuIdle(idle * 100.0 / total);
                data.setCpuUsage(100.0 - data.getCpuIdle());
            }
        }
    }
    
    /**
     * Collect memory information
     */
    private void collectMemoryInfo(MonitorData data) throws SSHException {
        String memInfo = execCommand("cat /proc/meminfo");
        
        Map<String, Long> mem = new HashMap<>();
        for (String line : memInfo.split("\n")) {
            String[] parts = line.split(":");
            if (parts.length >= 2) {
                String key = parts[0].trim();
                String value = parts[1].trim().split("\\s+")[0];
                try {
                    mem.put(key, Long.parseLong(value) * 1024); // Convert KB to bytes
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
        
        long total = mem.getOrDefault("MemTotal", 0L);
        long free = mem.getOrDefault("MemFree", 0L);
        long buffers = mem.getOrDefault("Buffers", 0L);
        long cached = mem.getOrDefault("Cached", 0L);
        long available = mem.getOrDefault("MemAvailable", free + buffers + cached);
        
        data.setMemTotal(total);
        data.setMemFree(free);
        data.setMemBuffers(buffers);
        data.setMemCached(cached);
        data.setMemUsed(total - available);
        
        if (total > 0) {
            data.setMemUsagePercent((total - available) * 100.0 / total);
        }
        
        // Swap
        data.setSwapTotal(mem.getOrDefault("SwapTotal", 0L));
        data.setSwapFree(mem.getOrDefault("SwapFree", 0L));
        data.setSwapUsed(data.getSwapTotal() - data.getSwapFree());
    }
    
    /**
     * Collect disk information
     */
    private void collectDiskInfo(MonitorData data) throws SSHException {
        String dfOutput = execCommand("df -B1 -x tmpfs -x devtmpfs -x squashfs 2>/dev/null | tail -n +2");
        
        for (String line : dfOutput.split("\n")) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 6) {
                MonitorData.DiskInfo disk = new MonitorData.DiskInfo();
                disk.setName(parts[0]);
                
                try {
                    disk.setTotal(Long.parseLong(parts[1]));
                    disk.setUsed(Long.parseLong(parts[2]));
                    disk.setFree(Long.parseLong(parts[3]));
                } catch (Exception e) {
                    continue;
                }
                
                // Usage percent (remove %)
                String usageStr = parts[4].replace("%", "");
                try {
                    disk.setUsagePercent(Double.parseDouble(usageStr));
                } catch (Exception e) {
                    // Ignore
                }
                
                disk.setMountPoint(parts[5]);
                data.addDisk(disk);
            }
        }
    }
    
    /**
     * Collect network information
     */
    private void collectNetworkInfo(MonitorData data) throws SSHException {
        String netDev = execCommand("cat /proc/net/dev | tail -n +3");
        
        long totalRx = 0;
        long totalTx = 0;
        
        for (String line : netDev.split("\n")) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.trim().split("[:\\s]+");
            if (parts.length >= 10) {
                String ifName = parts[0];
                
                // Skip loopback
                if ("lo".equals(ifName)) continue;
                
                MonitorData.NetworkInfo net = new MonitorData.NetworkInfo();
                net.setName(ifName);
                
                try {
                    long rxBytes = Long.parseLong(parts[1]);
                    long txBytes = Long.parseLong(parts[9]);
                    
                    net.setRxBytes(rxBytes);
                    net.setTxBytes(txBytes);
                    
                    totalRx += rxBytes;
                    totalTx += txBytes;
                } catch (Exception e) {
                    // Ignore
                }
                
                data.addNetwork(net);
            }
        }
        
        data.setNetRxBytes(totalRx);
        data.setNetTxBytes(totalTx);
        
        // Calculate speed
        long now = System.currentTimeMillis();
        if (prevTimestamp > 0) {
            double elapsed = (now - prevTimestamp) / 1000.0;
            if (elapsed > 0) {
                data.setNetRxSpeed((long) ((totalRx - prevRxBytes) / elapsed));
                data.setNetTxSpeed((long) ((totalTx - prevTxBytes) / elapsed));
            }
        }
        
        prevRxBytes = totalRx;
        prevTxBytes = totalTx;
        prevTimestamp = now;
    }
    
    /**
     * Collect process information
     */
    private void collectProcessInfo(MonitorData data) throws SSHException {
        // Process count
        String countStr = execCommand("ps aux | wc -l").trim();
        try {
            data.setProcessCount(Integer.parseInt(countStr) - 1); // Subtract header
        } catch (Exception e) {
            data.setProcessCount(0);
        }
        
        // Top processes by CPU
        String psOutput = execCommand("ps aux --sort=-%cpu | head -11 | tail -10");
        
        for (String line : psOutput.split("\n")) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.trim().split("\\s+", 11);
            if (parts.length >= 11) {
                MonitorData.ProcessInfo proc = new MonitorData.ProcessInfo();
                proc.setUser(parts[0]);
                
                try {
                    proc.setPid(Integer.parseInt(parts[1]));
                    proc.setCpuPercent(Double.parseDouble(parts[2]));
                    proc.setMemPercent(Double.parseDouble(parts[3]));
                } catch (Exception e) {
                    continue;
                }
                
                proc.setCommand(parts[10]);
                data.addProcess(proc);
            }
        }
    }
    
    /**
     * Execute SSH command
     */
    private String execCommand(String command) throws SSHException {
        return sshSession.exec(command);
    }
    
    // Listeners
    public void addListener(MonitorListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(MonitorListener listener) {
        listeners.remove(listener);
    }
    
    private void fireDataReceived(MonitorData data) {
        for (MonitorListener listener : listeners) {
            try {
                listener.onDataReceived(data);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
    
    private void fireError(String message) {
        for (MonitorListener listener : listeners) {
            try {
                listener.onError(message);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
    
    // Getters/Setters
    public boolean isRunning() {
        return running;
    }
    
    public int getIntervalSeconds() {
        return intervalSeconds;
    }
    
    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }
    
    /**
     * Monitor Listener
     */
    public interface MonitorListener {
        void onDataReceived(MonitorData data);
        void onError(String message);
    }
}
