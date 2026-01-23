package com.finalshell.monitor;

import java.util.*;
import java.util.regex.*;

/**
 * 监控数据解析器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class MonitorParser {
    
    public static CpuInfo parseCpuInfo(String output) {
        CpuInfo info = new CpuInfo();
        
        try {
            Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)%?\\s*(us|user)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(output);
            if (matcher.find()) {
                info.setUserPercent(Double.parseDouble(matcher.group(1)));
            }
            
            pattern = Pattern.compile("(\\d+\\.?\\d*)%?\\s*(sy|system)", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(output);
            if (matcher.find()) {
                info.setSystemPercent(Double.parseDouble(matcher.group(1)));
            }
            
            pattern = Pattern.compile("(\\d+\\.?\\d*)%?\\s*(id|idle)", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(output);
            if (matcher.find()) {
                info.setIdlePercent(Double.parseDouble(matcher.group(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return info;
    }
    
    public static MemInfo parseMemInfo(String output) {
        MemInfo info = new MemInfo();
        
        try {
            Pattern pattern = Pattern.compile("Mem:\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(output);
            if (matcher.find()) {
                info.setTotal(Long.parseLong(matcher.group(1)));
                info.setUsed(Long.parseLong(matcher.group(2)));
                info.setFree(Long.parseLong(matcher.group(3)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return info;
    }
    
    public static List<DiskInfo> parseDiskInfo(String output) {
        List<DiskInfo> list = new ArrayList<>();
        
        try {
            String[] lines = output.split("\n");
            for (String line : lines) {
                if (line.startsWith("/") || line.contains("/dev/")) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 6) {
                        DiskInfo info = new DiskInfo();
                        info.setFilesystem(parts[0]);
                        info.setTotal(parseSize(parts[1]));
                        info.setUsed(parseSize(parts[2]));
                        info.setAvailable(parseSize(parts[3]));
                        info.setMountPoint(parts[5]);
                        list.add(info);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    public static List<NetInfo> parseNetInfo(String output) {
        List<NetInfo> list = new ArrayList<>();
        
        try {
            String[] lines = output.split("\n");
            for (String line : lines) {
                if (line.contains(":") && !line.contains("lo:")) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 10) {
                        NetInfo info = new NetInfo();
                        info.setInterfaceName(parts[0].replace(":", ""));
                        info.setRxBytes(Long.parseLong(parts[1]));
                        info.setTxBytes(Long.parseLong(parts[9]));
                        list.add(info);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    private static long parseSize(String sizeStr) {
        try {
            sizeStr = sizeStr.toUpperCase().trim();
            long multiplier = 1;
            
            if (sizeStr.endsWith("K")) {
                multiplier = 1024;
                sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
            } else if (sizeStr.endsWith("M")) {
                multiplier = 1024 * 1024;
                sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
            } else if (sizeStr.endsWith("G")) {
                multiplier = 1024 * 1024 * 1024;
                sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
            } else if (sizeStr.endsWith("T")) {
                multiplier = 1024L * 1024 * 1024 * 1024;
                sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
            }
            
            return (long) (Double.parseDouble(sizeStr) * multiplier);
        } catch (Exception e) {
            return 0;
        }
    }
}
