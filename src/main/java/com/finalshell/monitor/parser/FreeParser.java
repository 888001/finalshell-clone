package com.finalshell.monitor.parser;

/**
 * free命令解析器 - 内存使用情况
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Monitor_DeepAnalysis.md
 * 
 * 命令: free
 * 输出示例:
 *               total        used        free      shared  buff/cache   available
 * Mem:       16384000     8192000     4096000      512000     4096000     7680000
 * Swap:       8192000      102400     8089600
 */
public class FreeParser extends BaseParser {
    
    private long memTotal;
    private long memUsed;
    private long memFree;
    private long memShared;
    private long memBuffCache;
    private long memAvailable;
    private long swapTotal;
    private long swapUsed;
    private long swapFree;
    
    @Override
    public void parse() {
        if (rawOutput == null) return;
        
        String[] lines = splitLines(rawOutput);
        
        for (String line : lines) {
            if (line.startsWith("Mem:")) {
                String[] parts = splitWhitespace(line);
                if (parts.length >= 7) {
                    memTotal = parseLong(parts[1], 0);
                    memUsed = parseLong(parts[2], 0);
                    memFree = parseLong(parts[3], 0);
                    memShared = parseLong(parts[4], 0);
                    memBuffCache = parseLong(parts[5], 0);
                    memAvailable = parseLong(parts[6], 0);
                } else if (parts.length >= 4) {
                    memTotal = parseLong(parts[1], 0);
                    memUsed = parseLong(parts[2], 0);
                    memFree = parseLong(parts[3], 0);
                }
            } else if (line.startsWith("Swap:")) {
                String[] parts = splitWhitespace(line);
                if (parts.length >= 4) {
                    swapTotal = parseLong(parts[1], 0);
                    swapUsed = parseLong(parts[2], 0);
                    swapFree = parseLong(parts[3], 0);
                }
            }
        }
    }
    
    public long getMemTotal() { return memTotal; }
    public long getMemUsed() { return memUsed; }
    public long getMemFree() { return memFree; }
    public long getMemShared() { return memShared; }
    public long getMemBuffCache() { return memBuffCache; }
    public long getMemAvailable() { return memAvailable; }
    public long getSwapTotal() { return swapTotal; }
    public long getSwapUsed() { return swapUsed; }
    public long getSwapFree() { return swapFree; }
    
    public double getMemUsagePercent() {
        if (memTotal == 0) return 0;
        return (double) memUsed / memTotal * 100;
    }
    
    public double getSwapUsagePercent() {
        if (swapTotal == 0) return 0;
        return (double) swapUsed / swapTotal * 100;
    }
}
