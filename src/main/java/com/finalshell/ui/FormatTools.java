package com.finalshell.ui;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 格式化工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - FormatTools
 */
public class FormatTools {
    
    private static final String[] SIZE_UNITS = {"B", "KB", "MB", "GB", "TB"};
    private static final DecimalFormat SIZE_FORMAT = new DecimalFormat("#.##");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private FormatTools() {}
    
    public static String formatFileSize(long bytes) {
        if (bytes < 0) return "0 B";
        
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < SIZE_UNITS.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return SIZE_FORMAT.format(size) + " " + SIZE_UNITS[unitIndex];
    }
    
    public static String formatSpeed(long bytesPerSecond) {
        return formatFileSize(bytesPerSecond) + "/s";
    }
    
    public static String formatTime(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }
    
    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds % 60);
        } else {
            return String.format("%d秒", seconds);
        }
    }
    
    public static String formatPercent(double ratio) {
        return SIZE_FORMAT.format(ratio * 100) + "%";
    }
    
    public static String formatETA(long bytesRemaining, long bytesPerSecond) {
        if (bytesPerSecond <= 0) return "--:--";
        long seconds = bytesRemaining / bytesPerSecond;
        return formatDuration(seconds * 1000);
    }
}
