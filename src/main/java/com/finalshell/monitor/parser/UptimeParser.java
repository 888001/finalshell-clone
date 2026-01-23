package com.finalshell.monitor.parser;

import java.util.regex.*;

/**
 * uptime命令解析器 - 系统负载
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Monitor_DeepAnalysis.md
 * 
 * 命令: uptime
 * 输出示例:
 * 10:30:01 up 5 days, 3:45, 2 users, load average: 0.15, 0.10, 0.05
 */
public class UptimeParser extends BaseParser {
    
    private String uptime;
    private int users;
    private double load1;
    private double load5;
    private double load15;
    
    @Override
    public void parse() {
        if (rawOutput == null) return;
        
        // 解析运行时间
        Pattern uptimePattern = Pattern.compile("up\\s+(.+?),\\s+\\d+\\s+user");
        Matcher uptimeMatcher = uptimePattern.matcher(rawOutput);
        if (uptimeMatcher.find()) {
            uptime = uptimeMatcher.group(1).trim();
        }
        
        // 解析用户数
        Pattern usersPattern = Pattern.compile("(\\d+)\\s+users?");
        Matcher usersMatcher = usersPattern.matcher(rawOutput);
        if (usersMatcher.find()) {
            users = parseInt(usersMatcher.group(1), 0);
        }
        
        // 解析负载
        Pattern loadPattern = Pattern.compile("load average:\\s*([\\d.]+),\\s*([\\d.]+),\\s*([\\d.]+)");
        Matcher loadMatcher = loadPattern.matcher(rawOutput);
        if (loadMatcher.find()) {
            load1 = parseDouble(loadMatcher.group(1), 0);
            load5 = parseDouble(loadMatcher.group(2), 0);
            load15 = parseDouble(loadMatcher.group(3), 0);
        }
    }
    
    public String getUptime() { return uptime; }
    public int getUsers() { return users; }
    public double getLoad1() { return load1; }
    public double getLoad5() { return load5; }
    public double getLoad15() { return load15; }
    
    public String getLoadString() {
        return String.format("%.2f, %.2f, %.2f", load1, load5, load15);
    }
}
