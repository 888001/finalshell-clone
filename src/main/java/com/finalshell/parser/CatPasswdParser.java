package com.finalshell.parser;

import com.finalshell.monitor.parser.BaseParser;
import java.util.*;

/**
 * /etc/passwd 解析器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CatPasswdParser extends BaseParser {
    
    private List<PasswdEntry> entries;
    
    public CatPasswdParser() {
        this.entries = new ArrayList<>();
    }
    
    @Override
    public void parse() {
        if (rawOutput == null || rawOutput.isEmpty()) return;
        
        entries.clear();
        String[] lines = rawOutput.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            String[] parts = line.split(":");
            if (parts.length >= 7) {
                PasswdEntry entry = new PasswdEntry();
                entry.username = parts[0];
                entry.password = parts[1];
                entry.uid = Integer.parseInt(parts[2]);
                entry.gid = Integer.parseInt(parts[3]);
                entry.gecos = parts[4];
                entry.homeDir = parts[5];
                entry.shell = parts[6];
                entries.add(entry);
            }
        }
    }
    
    public List<PasswdEntry> getEntries() {
        return entries;
    }
    
    public PasswdEntry findByUsername(String username) {
        for (PasswdEntry entry : entries) {
            if (entry.username.equals(username)) {
                return entry;
            }
        }
        return null;
    }
    
    public List<String> getUsernames() {
        List<String> usernames = new ArrayList<>();
        for (PasswdEntry entry : entries) {
            usernames.add(entry.username);
        }
        return usernames;
    }
    
    public static class PasswdEntry {
        public String username;
        public String password;
        public int uid;
        public int gid;
        public String gecos;
        public String homeDir;
        public String shell;
    }
}
