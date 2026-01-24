package com.finalshell.monitor;

/**
 * Memory Info - Represents memory usage information
 */
public class MemInfo {
    
    private long total;
    private long used;
    private long free;
    private long cached;
    private long buffers;
    private long swapTotal;
    private long swapUsed;
    private long swapFree;
    
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    
    public long getUsed() { return used; }
    public void setUsed(long used) { this.used = used; }
    
    public long getFree() { return free; }
    public void setFree(long free) { this.free = free; }
    
    public long getCached() { return cached; }
    public void setCached(long cached) { this.cached = cached; }
    
    public long getBuffers() { return buffers; }
    public void setBuffers(long buffers) { this.buffers = buffers; }
    
    public long getSwapTotal() { return swapTotal; }
    public void setSwapTotal(long swapTotal) { this.swapTotal = swapTotal; }
    
    public long getSwapUsed() { return swapUsed; }
    public void setSwapUsed(long swapUsed) { this.swapUsed = swapUsed; }
    
    public long getSwapFree() { return swapFree; }
    public void setSwapFree(long swapFree) { this.swapFree = swapFree; }
    
    public double getUsagePercent() {
        if (total == 0) return 0;
        return (double) used / total * 100;
    }
}
