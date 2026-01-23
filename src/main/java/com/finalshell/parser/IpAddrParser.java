package com.finalshell.parser;

import java.util.*;
import java.util.regex.*;

/**
 * ip addr 命令解析器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class IpAddrParser extends BaseParser {
    
    private List<NetInterface> interfaces;
    
    public IpAddrParser() {
        this.interfaces = new ArrayList<>();
    }
    
    @Override
    public void parse(String content) {
        if (content == null || content.isEmpty()) return;
        
        interfaces.clear();
        String[] lines = content.split("\n");
        NetInterface current = null;
        
        Pattern ifacePattern = Pattern.compile("^\\d+:\\s+(\\S+):");
        Pattern inetPattern = Pattern.compile("inet\\s+(\\S+)");
        Pattern inet6Pattern = Pattern.compile("inet6\\s+(\\S+)");
        Pattern macPattern = Pattern.compile("link/ether\\s+(\\S+)");
        
        for (String line : lines) {
            Matcher ifaceMatcher = ifacePattern.matcher(line);
            if (ifaceMatcher.find()) {
                if (current != null) {
                    interfaces.add(current);
                }
                current = new NetInterface();
                current.setName(ifaceMatcher.group(1));
                continue;
            }
            
            if (current == null) continue;
            
            Matcher inetMatcher = inetPattern.matcher(line);
            if (inetMatcher.find()) {
                current.setIpv4(inetMatcher.group(1));
            }
            
            Matcher inet6Matcher = inet6Pattern.matcher(line);
            if (inet6Matcher.find()) {
                current.setIpv6(inet6Matcher.group(1));
            }
            
            Matcher macMatcher = macPattern.matcher(line);
            if (macMatcher.find()) {
                current.setMac(macMatcher.group(1));
            }
        }
        
        if (current != null) {
            interfaces.add(current);
        }
    }
    
    public List<NetInterface> getInterfaces() {
        return interfaces;
    }
    
    public NetInterface findByName(String name) {
        for (NetInterface iface : interfaces) {
            if (name.equals(iface.getName())) {
                return iface;
            }
        }
        return null;
    }
}
