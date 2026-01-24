package com.finalshell.monitor;

/**
 * CPU信息数据类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CpuInfo {
    
    private String modelName;
    private int cores;
    private double mhz;
    private String vendor;
    private String cacheSize;
    private double userPercent;
    private double systemPercent;
    private double idlePercent;
    
    public CpuInfo() {
    }
    
    public CpuInfo(String modelName, int cores, double mhz) {
        this.modelName = modelName;
        this.cores = cores;
        this.mhz = mhz;
    }
    
    public String getModelName() {
        return modelName;
    }
    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public int getCores() {
        return cores;
    }
    
    public void setCores(int cores) {
        this.cores = cores;
    }
    
    public double getMhz() {
        return mhz;
    }
    
    public void setMhz(double mhz) {
        this.mhz = mhz;
    }
    
    public String getVendor() {
        return vendor;
    }
    
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    public String getCacheSize() {
        return cacheSize;
    }
    
    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public double getUserPercent() { return userPercent; }
    public void setUserPercent(double userPercent) { this.userPercent = userPercent; }
    
    public double getSystemPercent() { return systemPercent; }
    public void setSystemPercent(double systemPercent) { this.systemPercent = systemPercent; }
    
    public double getIdlePercent() { return idlePercent; }
    public void setIdlePercent(double idlePercent) { this.idlePercent = idlePercent; }
    
    @Override
    public String toString() {
        return String.format("%s (%d cores @ %.0f MHz)", modelName, cores, mhz);
    }
}
