package com.finalshell.monitor.parser;

/**
 * /proc/stat解析器 - CPU使用情况
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Monitor_DeepAnalysis.md
 * 
 * 命令: cat /proc/stat
 * 输出示例:
 * cpu  10132153 290696 3084719 46828483 16683 0 25195 0 0 0
 * cpu0 1393280 32966 572056 13343292 6130 0 17875 0 0 0
 */
public class ProcStatParser extends BaseParser {
    
    private long user;
    private long nice;
    private long system;
    private long idle;
    private long iowait;
    private long irq;
    private long softirq;
    private long steal;
    
    private long prevTotal;
    private long prevIdle;
    private double cpuUsage;
    
    @Override
    public void parse() {
        if (rawOutput == null) return;
        
        String[] lines = splitLines(rawOutput);
        
        for (String line : lines) {
            if (line.startsWith("cpu ")) {
                String[] parts = splitWhitespace(line);
                if (parts.length >= 5) {
                    user = parseLong(parts[1], 0);
                    nice = parseLong(parts[2], 0);
                    system = parseLong(parts[3], 0);
                    idle = parseLong(parts[4], 0);
                    
                    if (parts.length >= 6) iowait = parseLong(parts[5], 0);
                    if (parts.length >= 7) irq = parseLong(parts[6], 0);
                    if (parts.length >= 8) softirq = parseLong(parts[7], 0);
                    if (parts.length >= 9) steal = parseLong(parts[8], 0);
                    
                    calculateUsage();
                }
                break;
            }
        }
    }
    
    private void calculateUsage() {
        long total = user + nice + system + idle + iowait + irq + softirq + steal;
        long totalIdle = idle + iowait;
        
        if (prevTotal > 0) {
            long totalDelta = total - prevTotal;
            long idleDelta = totalIdle - prevIdle;
            
            if (totalDelta > 0) {
                cpuUsage = (1.0 - (double) idleDelta / totalDelta) * 100;
            }
        }
        
        prevTotal = total;
        prevIdle = totalIdle;
    }
    
    public double getCpuUsage() { return cpuUsage; }
    public long getUser() { return user; }
    public long getNice() { return nice; }
    public long getSystem() { return system; }
    public long getIdle() { return idle; }
    public long getIowait() { return iowait; }
    public long getIrq() { return irq; }
    public long getSoftirq() { return softirq; }
    public long getSteal() { return steal; }
}
