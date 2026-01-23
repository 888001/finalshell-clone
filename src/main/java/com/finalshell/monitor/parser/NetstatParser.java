package com.finalshell.monitor.parser;

import com.finalshell.network.NetRow;

import java.util.*;
import java.util.regex.*;

/**
 * Netstat命令输出解析器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class NetstatParser extends BaseParser {
    
    private static final Pattern TCP_PATTERN = Pattern.compile(
        "^(tcp[46]?)\\s+\\d+\\s+\\d+\\s+(\\S+):(\\d+)\\s+(\\S+):(\\d+|\\*)\\s+(\\S+)(?:\\s+(\\d+)/(.+))?",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern UDP_PATTERN = Pattern.compile(
        "^(udp[46]?)\\s+\\d+\\s+\\d+\\s+(\\S+):(\\d+)\\s+(\\S+):(\\d+|\\*)(?:\\s+(\\d+)/(.+))?",
        Pattern.CASE_INSENSITIVE
    );
    
    public List<NetRow> parse(String output) {
        List<NetRow> list = new ArrayList<>();
        
        if (output == null || output.isEmpty()) {
            return list;
        }
        
        String[] lines = output.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("Active") || line.startsWith("Proto")) {
                continue;
            }
            
            NetRow row = parseLine(line);
            if (row != null) {
                list.add(row);
            }
        }
        
        return list;
    }
    
    private NetRow parseLine(String line) {
        Matcher tcpMatcher = TCP_PATTERN.matcher(line);
        if (tcpMatcher.find()) {
            return createNetRow(tcpMatcher, true);
        }
        
        Matcher udpMatcher = UDP_PATTERN.matcher(line);
        if (udpMatcher.find()) {
            return createNetRow(udpMatcher, false);
        }
        
        return null;
    }
    
    private NetRow createNetRow(Matcher matcher, boolean hasTcpState) {
        NetRow row = new NetRow();
        
        row.setProtocol(matcher.group(1).toUpperCase());
        row.setLocalAddress(matcher.group(2));
        row.setLocalPort(parsePort(matcher.group(3)));
        row.setRemoteAddress(matcher.group(4));
        row.setRemotePort(parsePort(matcher.group(5)));
        
        if (hasTcpState) {
            row.setState(matcher.group(6));
            if (matcher.group(7) != null) {
                row.setPid(Integer.parseInt(matcher.group(7)));
            }
            if (matcher.group(8) != null) {
                row.setProcessName(matcher.group(8));
            }
        } else {
            row.setState("-");
            if (matcher.group(6) != null) {
                row.setPid(Integer.parseInt(matcher.group(6)));
            }
            if (matcher.group(7) != null) {
                row.setProcessName(matcher.group(7));
            }
        }
        
        return row;
    }
    
    private int parsePort(String portStr) {
        if (portStr == null || portStr.equals("*")) {
            return 0;
        }
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public List<NetRow> parseSSOutput(String output) {
        List<NetRow> list = new ArrayList<>();
        
        if (output == null || output.isEmpty()) {
            return list;
        }
        
        String[] lines = output.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("State") || line.startsWith("Netid")) {
                continue;
            }
            
            String[] parts = line.split("\\s+");
            if (parts.length >= 5) {
                NetRow row = new NetRow();
                row.setProtocol(parts[0].toUpperCase());
                row.setState(parts[1]);
                
                String localEndpoint = parts[4];
                int lastColon = localEndpoint.lastIndexOf(':');
                if (lastColon > 0) {
                    row.setLocalAddress(localEndpoint.substring(0, lastColon));
                    row.setLocalPort(parsePort(localEndpoint.substring(lastColon + 1)));
                }
                
                if (parts.length >= 6) {
                    String remoteEndpoint = parts[5];
                    lastColon = remoteEndpoint.lastIndexOf(':');
                    if (lastColon > 0) {
                        row.setRemoteAddress(remoteEndpoint.substring(0, lastColon));
                        row.setRemotePort(parsePort(remoteEndpoint.substring(lastColon + 1)));
                    }
                }
                
                list.add(row);
            }
        }
        
        return list;
    }
}
