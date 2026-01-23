package com.finalshell.update;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * 更新配置创建器
 * 用于生成更新配置文件
 */
public class UpdateConfigCreater {
    
    private String version;
    private String baseUrl;
    private List<FileInfo> files = new ArrayList<>();
    
    public UpdateConfigCreater() {}
    
    public UpdateConfigCreater(String version, String baseUrl) {
        this.version = version;
        this.baseUrl = baseUrl;
    }
    
    public void addFile(String path, long size, String md5) {
        files.add(new FileInfo(path, size, md5));
    }
    
    public void scanDirectory(File dir, String basePath) {
        if (!dir.exists() || !dir.isDirectory()) return;
        
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(file, basePath + file.getName() + "/");
            } else {
                String md5 = calculateMD5(file);
                addFile(basePath + file.getName(), file.length(), md5);
            }
        }
    }
    
    public String generateConfig() {
        StringBuilder sb = new StringBuilder();
        sb.append("version=").append(version).append("\n");
        sb.append("baseUrl=").append(baseUrl).append("\n");
        sb.append("fileCount=").append(files.size()).append("\n");
        
        for (int i = 0; i < files.size(); i++) {
            FileInfo fi = files.get(i);
            sb.append("file.").append(i).append(".path=").append(fi.path).append("\n");
            sb.append("file.").append(i).append(".size=").append(fi.size).append("\n");
            sb.append("file.").append(i).append(".md5=").append(fi.md5).append("\n");
        }
        
        return sb.toString();
    }
    
    public void saveConfig(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(generateConfig());
        }
    }
    
    private String calculateMD5(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, len);
                }
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    public void setVersion(String version) { this.version = version; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    
    private static class FileInfo {
        String path;
        long size;
        String md5;
        
        FileInfo(String path, long size, String md5) {
            this.path = path;
            this.size = size;
            this.md5 = md5;
        }
    }
}
