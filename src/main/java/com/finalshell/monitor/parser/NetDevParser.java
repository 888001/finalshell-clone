package com.finalshell.monitor.parser;

import java.util.*;

/**
 * /proc/net/dev解析器 - 网络流量
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Monitor_DeepAnalysis.md
 * 
 * 命令: cat /proc/net/dev
 * 输出示例:
 * Inter-|   Receive                                                |  Transmit
 *  face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets errs drop fifo colls carrier compressed
 *     lo: 1000000  10000    0    0    0     0          0         0  1000000  10000    0    0    0     0       0          0
 *   eth0: 5000000  50000    0    0    0     0          0         0  2500000  25000    0    0    0     0       0          0
 */
public class NetDevParser extends BaseParser {
    
    private final Map<String, NetInterface> interfaces = new LinkedHashMap<>();
    private long prevTimestamp;
    
    @Override
    public void parse() {
        if (rawOutput == null) return;
        
        long currentTime = System.currentTimeMillis();
        String[] lines = splitLines(rawOutput);
        
        for (String line : lines) {
            if (line.contains("|") || line.trim().isEmpty()) {
                continue;
            }
            
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String name = line.substring(0, colonIndex).trim();
                String data = line.substring(colonIndex + 1).trim();
                String[] parts = splitWhitespace(data);
                
                if (parts.length >= 16) {
                    NetInterface iface = interfaces.computeIfAbsent(name, k -> new NetInterface(k));
                    
                    long rxBytes = parseLong(parts[0], 0);
                    long rxPackets = parseLong(parts[1], 0);
                    long rxErrors = parseLong(parts[2], 0);
                    long rxDropped = parseLong(parts[3], 0);
                    
                    long txBytes = parseLong(parts[8], 0);
                    long txPackets = parseLong(parts[9], 0);
                    long txErrors = parseLong(parts[10], 0);
                    long txDropped = parseLong(parts[11], 0);
                    
                    // 计算速率
                    if (prevTimestamp > 0 && iface.rxBytes > 0) {
                        double timeDelta = (currentTime - prevTimestamp) / 1000.0;
                        if (timeDelta > 0) {
                            iface.rxSpeed = (rxBytes - iface.rxBytes) / timeDelta;
                            iface.txSpeed = (txBytes - iface.txBytes) / timeDelta;
                        }
                    }
                    
                    iface.rxBytes = rxBytes;
                    iface.rxPackets = rxPackets;
                    iface.rxErrors = rxErrors;
                    iface.rxDropped = rxDropped;
                    iface.txBytes = txBytes;
                    iface.txPackets = txPackets;
                    iface.txErrors = txErrors;
                    iface.txDropped = txDropped;
                }
            }
        }
        
        prevTimestamp = currentTime;
    }
    
    public Map<String, NetInterface> getInterfaces() {
        return interfaces;
    }
    
    public NetInterface getInterface(String name) {
        return interfaces.get(name);
    }
    
    public double getTotalRxSpeed() {
        double total = 0;
        for (NetInterface iface : interfaces.values()) {
            if (!iface.name.equals("lo")) {
                total += iface.rxSpeed;
            }
        }
        return total;
    }
    
    public double getTotalTxSpeed() {
        double total = 0;
        for (NetInterface iface : interfaces.values()) {
            if (!iface.name.equals("lo")) {
                total += iface.txSpeed;
            }
        }
        return total;
    }
    
    public static class NetInterface {
        public String name;
        public long rxBytes;
        public long rxPackets;
        public long rxErrors;
        public long rxDropped;
        public long txBytes;
        public long txPackets;
        public long txErrors;
        public long txDropped;
        public double rxSpeed;  // bytes/sec
        public double txSpeed;  // bytes/sec
        
        public NetInterface(String name) {
            this.name = name;
        }
        
        public String getFormattedRxSpeed() {
            return formatSpeed(rxSpeed);
        }
        
        public String getFormattedTxSpeed() {
            return formatSpeed(txSpeed);
        }
        
        private String formatSpeed(double bytesPerSec) {
            if (bytesPerSec < 1024) return String.format("%.0f B/s", bytesPerSec);
            if (bytesPerSec < 1024 * 1024) return String.format("%.1f KB/s", bytesPerSec / 1024);
            return String.format("%.1f MB/s", bytesPerSec / (1024 * 1024));
        }
    }
}
