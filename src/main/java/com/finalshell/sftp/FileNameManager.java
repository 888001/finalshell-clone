package com.finalshell.sftp;

/**
 * 文件名管理器
 * 处理SFTP文件名编码和转换
 */
public class FileNameManager {
    
    private static FileNameManager instance;
    private String encoding = "UTF-8";
    
    private FileNameManager() {}
    
    public static synchronized FileNameManager getInstance() {
        if (instance == null) {
            instance = new FileNameManager();
        }
        return instance;
    }
    
    public String encode(String filename) {
        if (filename == null) return null;
        try {
            return new String(filename.getBytes(encoding), encoding);
        } catch (Exception e) {
            return filename;
        }
    }
    
    public String decode(String filename) {
        if (filename == null) return null;
        try {
            return new String(filename.getBytes(), encoding);
        } catch (Exception e) {
            return filename;
        }
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public String normalize(String path) {
        if (path == null) return null;
        return path.replace("\\", "/");
    }
}
