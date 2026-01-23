package com.finalshell.network;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Whois查询类
 * 执行域名和IP的Whois查询
 */
public class WhoisQuery {
    
    private static final int DEFAULT_PORT = 43;
    private static final int TIMEOUT = 10000;
    
    private static final Map<String, String> WHOIS_SERVERS = new HashMap<>();
    
    static {
        WHOIS_SERVERS.put("com", "whois.verisign-grs.com");
        WHOIS_SERVERS.put("net", "whois.verisign-grs.com");
        WHOIS_SERVERS.put("org", "whois.pir.org");
        WHOIS_SERVERS.put("info", "whois.afilias.net");
        WHOIS_SERVERS.put("biz", "whois.biz");
        WHOIS_SERVERS.put("cn", "whois.cnnic.cn");
        WHOIS_SERVERS.put("jp", "whois.jprs.jp");
        WHOIS_SERVERS.put("kr", "whois.kr");
        WHOIS_SERVERS.put("uk", "whois.nic.uk");
        WHOIS_SERVERS.put("de", "whois.denic.de");
        WHOIS_SERVERS.put("fr", "whois.nic.fr");
        WHOIS_SERVERS.put("ru", "whois.tcinet.ru");
        WHOIS_SERVERS.put("default", "whois.iana.org");
    }
    
    public String query(String target) {
        if (target == null || target.isEmpty()) {
            return "Invalid target";
        }
        
        String server = getWhoisServer(target);
        return queryServer(server, target);
    }
    
    public String queryServer(String server, String target) {
        StringBuilder result = new StringBuilder();
        
        try (Socket socket = new Socket(server, DEFAULT_PORT)) {
            socket.setSoTimeout(TIMEOUT);
            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            
            out.println(target);
            
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            result.append("Error: ").append(e.getMessage());
        }
        
        return result.toString();
    }
    
    private String getWhoisServer(String target) {
        if (isIPAddress(target)) {
            return "whois.arin.net";
        }
        
        String tld = extractTLD(target);
        return WHOIS_SERVERS.getOrDefault(tld, WHOIS_SERVERS.get("default"));
    }
    
    private String extractTLD(String domain) {
        int lastDot = domain.lastIndexOf('.');
        if (lastDot > 0 && lastDot < domain.length() - 1) {
            return domain.substring(lastDot + 1).toLowerCase();
        }
        return "default";
    }
    
    private boolean isIPAddress(String target) {
        return target.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    }
    
    public static void addServer(String tld, String server) {
        WHOIS_SERVERS.put(tld, server);
    }
}
