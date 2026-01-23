package com.finalshell.monitor;

import com.finalshell.ssh.SSHSession;

import java.util.*;
import java.util.concurrent.*;

/**
 * Shell监控扫描器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class MonitorScannerShell {
    
    private SSHSession session;
    private ScheduledExecutorService scheduler;
    private List<MonitorListener> listeners;
    private boolean running;
    private int interval;
    private Map<String, String> commandMap;
    
    public MonitorScannerShell(SSHSession session) {
        this.session = session;
        this.listeners = new ArrayList<>();
        this.interval = 3000;
        this.commandMap = new HashMap<>();
        initCommands();
    }
    
    private void initCommands() {
        commandMap.put("cpu", "cat /proc/stat | head -1");
        commandMap.put("memory", "free -b");
        commandMap.put("disk", "df -B1");
        commandMap.put("network", "cat /proc/net/dev");
        commandMap.put("uptime", "uptime");
        commandMap.put("hostname", "hostname");
        commandMap.put("uname", "uname -a");
        commandMap.put("processes", "ps aux --no-headers | wc -l");
    }
    
    public void start() {
        if (running) {
            return;
        }
        
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::scan, 0, interval, TimeUnit.MILLISECONDS);
    }
    
    public void stop() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
    }
    
    private void scan() {
        if (!running || session == null || !session.isConnected()) {
            return;
        }
        
        try {
            MonitorData data = new MonitorData();
            
            for (Map.Entry<String, String> entry : commandMap.entrySet()) {
                String type = entry.getKey();
                String command = entry.getValue();
                String output = executeCommand(command);
                parseOutput(data, type, output);
            }
            
            notifyListeners(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String executeCommand(String command) {
        if (session == null) {
            return "";
        }
        try {
            return session.executeCommand(command);
        } catch (Exception e) {
            return "";
        }
    }
    
    private void parseOutput(MonitorData data, String type, String output) {
        if (output == null || output.isEmpty()) {
            return;
        }
        
        switch (type) {
            case "hostname":
                data.setHostname(output.trim());
                break;
            case "uname":
                parseUname(data, output);
                break;
            case "uptime":
                parseUptime(data, output);
                break;
            case "cpu":
                parseCpu(data, output);
                break;
            case "memory":
                parseMemory(data, output);
                break;
        }
    }
    
    private void parseUname(MonitorData data, String output) {
        String[] parts = output.split("\\s+");
        if (parts.length >= 1) {
            data.setOsName(parts[0]);
        }
        if (parts.length >= 3) {
            data.setKernelVersion(parts[2]);
        }
    }
    
    private void parseUptime(MonitorData data, String output) {
        if (output.contains("load average:")) {
            String loadPart = output.substring(output.indexOf("load average:") + 14);
            String[] loads = loadPart.split(",");
            if (loads.length >= 3) {
                try {
                    data.setLoad1(Double.parseDouble(loads[0].trim()));
                    data.setLoad5(Double.parseDouble(loads[1].trim()));
                    data.setLoad15(Double.parseDouble(loads[2].trim()));
                } catch (NumberFormatException e) {
                }
            }
        }
        
        if (output.contains("up ")) {
            int upIndex = output.indexOf("up ");
            int commaIndex = output.indexOf(",", upIndex);
            if (commaIndex > upIndex) {
                data.setUptime(output.substring(upIndex + 3, commaIndex).trim());
            }
        }
    }
    
    private void parseCpu(MonitorData data, String output) {
        String[] parts = output.split("\\s+");
        if (parts.length >= 5) {
            try {
                long user = Long.parseLong(parts[1]);
                long nice = Long.parseLong(parts[2]);
                long system = Long.parseLong(parts[3]);
                long idle = Long.parseLong(parts[4]);
                
                long total = user + nice + system + idle;
                if (total > 0) {
                    double usage = 100.0 * (user + nice + system) / total;
                    data.setCpuUsage(usage);
                }
            } catch (NumberFormatException e) {
            }
        }
    }
    
    private void parseMemory(MonitorData data, String output) {
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.startsWith("Mem:")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    try {
                        data.setTotalMemory(Long.parseLong(parts[1]));
                        data.setUsedMemory(Long.parseLong(parts[2]));
                    } catch (NumberFormatException e) {
                    }
                }
                break;
            }
        }
    }
    
    public void addListener(MonitorListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(MonitorListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(MonitorData data) {
        for (MonitorListener listener : listeners) {
            listener.onMonitorUpdate(data);
        }
    }
    
    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    public int getInterval() {
        return interval;
    }
    
    public boolean isRunning() {
        return running;
    }
}
