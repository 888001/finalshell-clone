package com.finalshell.monitor.parser;

import java.util.*;

/**
 * df命令解析器 - 磁盘使用情况
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Monitor_DeepAnalysis.md
 * 
 * 命令: df
 * 输出示例:
 * Filesystem     1K-blocks      Used Available Use% Mounted on
 * /dev/sda1      102400000  51200000  51200000  50% /
 * /dev/sdb1      204800000 102400000 102400000  50% /data
 */
public class DfParser extends BaseParser {
    
    private final List<DiskInfo> disks = new ArrayList<>();
    
    @Override
    public void parse() {
        disks.clear();
        if (rawOutput == null) return;
        
        String[] lines = splitLines(rawOutput);
        
        for (String line : lines) {
            if (line.startsWith("Filesystem") || line.trim().isEmpty()) {
                continue;
            }
            
            String[] parts = splitWhitespace(line);
            if (parts.length >= 6) {
                DiskInfo disk = new DiskInfo();
                disk.filesystem = parts[0];
                disk.blocks = parseLong(parts[1], 0);
                disk.used = parseLong(parts[2], 0);
                disk.available = parseLong(parts[3], 0);
                
                String usePercent = parts[4].replace("%", "");
                disk.usePercent = parseInt(usePercent, 0);
                
                disk.mountPoint = parts[5];
                
                // 只保留实际磁盘分区
                if (disk.filesystem.startsWith("/dev/") || 
                    disk.filesystem.contains("://") ||
                    disk.mountPoint.equals("/")) {
                    disks.add(disk);
                }
            }
        }
    }
    
    public List<DiskInfo> getDisks() {
        return disks;
    }
    
    public long getTotalBlocks() {
        long total = 0;
        for (DiskInfo disk : disks) {
            total += disk.blocks;
        }
        return total;
    }
    
    public long getTotalUsed() {
        long total = 0;
        for (DiskInfo disk : disks) {
            total += disk.used;
        }
        return total;
    }
    
    public static class DiskInfo {
        public String filesystem;
        public long blocks;
        public long used;
        public long available;
        public int usePercent;
        public String mountPoint;
        
        public String getFormattedSize() {
            return formatSize(blocks * 1024);
        }
        
        public String getFormattedUsed() {
            return formatSize(used * 1024);
        }
        
        public String getFormattedAvailable() {
            return formatSize(available * 1024);
        }
        
        private String formatSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
            return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
}
