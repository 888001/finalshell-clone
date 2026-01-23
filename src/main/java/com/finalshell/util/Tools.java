package com.finalshell.util;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

/**
 * 综合工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Tools_Analysis.md
 */
public class Tools {
    
    public static final String AES_KEY = "cwX5*ZKc$xCpz6dS";
    public static final long TIMESTAMP_SEED = 3680984568597093857L;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat SIZE_FORMAT = new DecimalFormat("#.##");
    private static float hidpiScale = 1.0f;
    
    private Tools() {}
    
    public static void setHidpiScale(float scale) {
        hidpiScale = scale;
    }
    
    public static float getHidpiScale() {
        return hidpiScale;
    }
    
    public static String formatFileSize(long bytes) {
        if (bytes < 0) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return SIZE_FORMAT.format(size) + " " + units[unitIndex];
    }
    
    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }
    
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }
    
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
    public static void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }
    
    public static String getFromClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        
        String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return Pattern.matches(ipPattern, ip);
    }
    
    public static boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }
    
    public static boolean isValidHostname(String hostname) {
        if (hostname == null || hostname.isEmpty()) return false;
        
        String hostnamePattern = "^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)*$";
        return Pattern.matches(hostnamePattern, hostname);
    }
    
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public static String getFileExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex + 1) : "";
    }
    
    public static String getFileName(String path) {
        if (path == null) return "";
        int slashIndex = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return slashIndex >= 0 ? path.substring(slashIndex + 1) : path;
    }
    
    public static String getParentPath(String path) {
        if (path == null) return "";
        int slashIndex = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return slashIndex > 0 ? path.substring(0, slashIndex) : "/";
    }
    
    public static void openInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
    
    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
    
    public static boolean isLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("linux") || osName.contains("unix");
    }
    
    public static String getOSName() {
        return System.getProperty("os.name");
    }
    
    public static String getOSVersion() {
        return System.getProperty("os.version");
    }
    
    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }
    
    public static String getUserHome() {
        return System.getProperty("user.home");
    }
    
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }
}
