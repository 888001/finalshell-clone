package com.finalshell.monitor;

import com.finalshell.ssh.SSHSession;

import java.util.*;
import java.util.concurrent.*;

/**
 * 监控扫描器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class MonitorScanner {
    
    private SSHSession session;
    private ScheduledExecutorService executor;
    private List<MonitorListener> listeners = new ArrayList<>();
    private volatile boolean running = false;
    
    private int scanInterval = 2000;
    
    private String cpuCommand = "top -bn1 | head -5";
    private String memCommand = "free -b";
    private String diskCommand = "df -h";
    private String netCommand = "cat /proc/net/dev";
    
    public MonitorScanner(SSHSession session) {
        this.session = session;
    }
    
    public void start() {
        if (running) {
            return;
        }
        
        running = true;
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::scan, 0, scanInterval, TimeUnit.MILLISECONDS);
    }
    
    public void stop() {
        running = false;
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }
    
    private void scan() {
        if (!running || session == null || !session.isConnected()) {
            return;
        }
        
        try {
            MonitorData data = new MonitorData();
            
            String cpuOutput = session.execCommand(cpuCommand);
            if (cpuOutput != null) {
                data.setCpuInfo(MonitorParser.parseCpuInfo(cpuOutput));
            }
            
            String memOutput = session.execCommand(memCommand);
            if (memOutput != null) {
                data.setMemInfo(MonitorParser.parseMemInfo(memOutput));
            }
            
            String diskOutput = session.execCommand(diskCommand);
            if (diskOutput != null) {
                data.setDiskInfoList(MonitorParser.parseDiskInfo(diskOutput));
            }
            
            String netOutput = session.execCommand(netCommand);
            if (netOutput != null) {
                data.setNetInfoList(MonitorParser.parseNetInfo(netOutput));
            }
            
            data.setTimestamp(System.currentTimeMillis());
            
            notifyListeners(data);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void notifyListeners(MonitorData data) {
        for (MonitorListener listener : listeners) {
            try {
                listener.onMonitorData(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void addListener(MonitorListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(MonitorListener listener) {
        listeners.remove(listener);
    }
    
    public void setScanInterval(int interval) {
        this.scanInterval = interval;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public interface MonitorListener {
        void onMonitorData(MonitorData data);
    }
}
