package com.finalshell.network;

import java.io.*;
import java.net.*;

/**
 * Whois查询
 */
public class WhoisQurey {
    
    private static final String DEFAULT_SERVER = "whois.iana.org";
    private static final int DEFAULT_PORT = 43;
    private static final int TIMEOUT = 10000;
    
    public static String query(String domain) throws IOException {
        return query(domain, DEFAULT_SERVER, DEFAULT_PORT);
    }
    
    public static String query(String domain, String server, int port) throws IOException {
        StringBuilder result = new StringBuilder();
        
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(server, port), TIMEOUT);
            socket.setSoTimeout(TIMEOUT);
            
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                out.println(domain);
                
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line).append("\n");
                }
            }
        }
        
        return result.toString();
    }
    
    public static String findWhoisServer(String domain) throws IOException {
        String result = query(domain, DEFAULT_SERVER, DEFAULT_PORT);
        String[] lines = result.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().startsWith("whois:")) {
                return line.substring(6).trim();
            }
        }
        return null;
    }
}
