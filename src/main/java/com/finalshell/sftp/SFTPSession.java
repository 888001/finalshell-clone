package com.finalshell.sftp;

import com.finalshell.config.ConnectConfig;
import com.finalshell.ssh.SSHException;
import com.finalshell.ssh.SSHSession;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SFTP Session - Wraps JSch ChannelSftp for file operations
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SFTP_DeepAnalysis.md, FtpTransfer_Event_DeepAnalysis.md
 */
public class SFTPSession {
    
    private static final Logger logger = LoggerFactory.getLogger(SFTPSession.class);
    
    private final SSHSession sshSession;
    private ChannelSftp channel;
    private String currentRemotePath = "/";
    private final List<SFTPEventListener> listeners = new CopyOnWriteArrayList<>();
    
    public SFTPSession(SSHSession sshSession) {
        this.sshSession = sshSession;
    }
    
    /**
     * Open SFTP channel
     */
    public void open() throws SSHException {
        channel = sshSession.openSftp();
        try {
            currentRemotePath = channel.pwd();
            logger.info("SFTP opened, current path: {}", currentRemotePath);
        } catch (SftpException e) {
            currentRemotePath = "/";
        }
    }
    
    /**
     * Close SFTP channel
     */
    public void close() {
        if (channel != null) {
            channel.disconnect();
            channel = null;
        }
    }
    
    /**
     * List files in directory
     */
    @SuppressWarnings("unchecked")
    public List<RemoteFile> listFiles(String path) throws SFTPException {
        checkChannel();
        List<RemoteFile> files = new ArrayList<>();
        
        // Normalize path
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        
        try {
            // Try to cd first to validate path, fallback to root if fails
            String absPath;
            try {
                channel.cd(path);
                absPath = channel.pwd();
            } catch (SftpException cdEx) {
                // Path doesn't exist, try root
                logger.warn("Cannot cd to {}, falling back to /", path);
                channel.cd("/");
                absPath = "/";
            }
            Vector<ChannelSftp.LsEntry> entries = channel.ls(".");
            for (ChannelSftp.LsEntry entry : entries) {
                String name = entry.getFilename();
                if (".".equals(name)) continue;
                
                SftpATTRS attrs = entry.getAttrs();
                RemoteFile file = new RemoteFile();
                file.setName(name);
                file.setPath(absPath.endsWith("/") ? absPath + name : absPath + "/" + name);
                file.setDirectory(attrs.isDir());
                file.setLink(attrs.isLink());
                file.setSize(attrs.getSize());
                file.setPermissions(attrs.getPermissionsString());
                file.setModifyTime(attrs.getMTime() * 1000L);
                file.setUid(attrs.getUId());
                file.setGid(attrs.getGId());
                
                files.add(file);
            }
            
            // Sort: directories first, then by name
            files.sort((a, b) -> {
                if (a.isDirectory() != b.isDirectory()) {
                    return a.isDirectory() ? -1 : 1;
                }
                return a.getName().compareToIgnoreCase(b.getName());
            });
            
        } catch (SftpException e) {
            throw new SFTPException("Failed to list files: " + e.getMessage(), e);
        }
        
        return files;
    }
    
    /**
     * Change remote directory
     */
    public void cd(String path) throws SFTPException {
        checkChannel();
        try {
            channel.cd(path);
            currentRemotePath = channel.pwd();
            logger.debug("Changed to: {}", currentRemotePath);
        } catch (SftpException e) {
            throw new SFTPException("Failed to change directory: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get current remote path
     */
    public String pwd() throws SFTPException {
        checkChannel();
        try {
            return channel.pwd();
        } catch (SftpException e) {
            throw new SFTPException("Failed to get current path: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create directory
     */
    public void mkdir(String path) throws SFTPException {
        checkChannel();
        try {
            channel.mkdir(path);
            logger.info("Created directory: {}", path);
            fireEvent(SFTPEvent.MKDIR, path);
        } catch (SftpException e) {
            throw new SFTPException("Failed to create directory: " + e.getMessage(), e);
        }
    }
    
    /**
     * Remove file or directory
     */
    public void rm(String path, boolean isDirectory) throws SFTPException {
        checkChannel();
        try {
            if (isDirectory) {
                rmdir(path);
            } else {
                channel.rm(path);
            }
            logger.info("Removed: {}", path);
            fireEvent(SFTPEvent.DELETE, path);
        } catch (SftpException e) {
            throw new SFTPException("Failed to remove: " + e.getMessage(), e);
        }
    }
    
    /**
     * Remove directory recursively
     */
    private void rmdir(String path) throws SftpException {
        List<RemoteFile> files;
        try {
            files = listFiles(path);
        } catch (SFTPException e) {
            throw new SftpException(0, e.getMessage());
        }
        
        for (RemoteFile file : files) {
            if ("..".equals(file.getName())) continue;
            
            if (file.isDirectory()) {
                rmdir(file.getPath());
            } else {
                channel.rm(file.getPath());
            }
        }
        channel.rmdir(path);
    }
    
    /**
     * Rename file or directory
     */
    public void rename(String oldPath, String newPath) throws SFTPException {
        checkChannel();
        try {
            channel.rename(oldPath, newPath);
            logger.info("Renamed: {} -> {}", oldPath, newPath);
            fireEvent(SFTPEvent.RENAME, newPath);
        } catch (SftpException e) {
            throw new SFTPException("Failed to rename: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get file attributes
     */
    public RemoteFile stat(String path) throws SFTPException {
        checkChannel();
        try {
            SftpATTRS attrs = channel.stat(path);
            RemoteFile file = new RemoteFile();
            file.setPath(path);
            file.setName(path.substring(path.lastIndexOf('/') + 1));
            file.setDirectory(attrs.isDir());
            file.setLink(attrs.isLink());
            file.setSize(attrs.getSize());
            file.setPermissions(attrs.getPermissionsString());
            file.setModifyTime(attrs.getMTime() * 1000L);
            return file;
        } catch (SftpException e) {
            throw new SFTPException("Failed to get file info: " + e.getMessage(), e);
        }
    }
    
    /**
     * Download file
     */
    public void download(String remotePath, String localPath, TransferProgressListener progress) throws SFTPException {
        checkChannel();
        try {
            fireEvent(SFTPEvent.DOWNLOAD_START, remotePath);
            
            SftpProgressMonitor monitor = progress != null ? new ProgressMonitorAdapter(progress) : null;
            channel.get(remotePath, localPath, monitor);
            
            fireEvent(SFTPEvent.DOWNLOAD_COMPLETE, remotePath);
            logger.info("Downloaded: {} -> {}", remotePath, localPath);
        } catch (SftpException e) {
            fireEvent(SFTPEvent.DOWNLOAD_ERROR, remotePath);
            throw new SFTPException("Download failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Download file to stream
     */
    public InputStream downloadStream(String remotePath) throws SFTPException {
        checkChannel();
        try {
            return channel.get(remotePath);
        } catch (SftpException e) {
            throw new SFTPException("Download failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Upload file
     */
    public void upload(String localPath, String remotePath, TransferProgressListener progress) throws SFTPException {
        checkChannel();
        try {
            fireEvent(SFTPEvent.UPLOAD_START, remotePath);
            
            SftpProgressMonitor monitor = progress != null ? new ProgressMonitorAdapter(progress) : null;
            channel.put(localPath, remotePath, monitor);
            
            fireEvent(SFTPEvent.UPLOAD_COMPLETE, remotePath);
            logger.info("Uploaded: {} -> {}", localPath, remotePath);
        } catch (SftpException e) {
            fireEvent(SFTPEvent.UPLOAD_ERROR, remotePath);
            throw new SFTPException("Upload failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Upload from stream
     */
    public void uploadStream(InputStream is, String remotePath) throws SFTPException {
        checkChannel();
        try {
            channel.put(is, remotePath);
            fireEvent(SFTPEvent.UPLOAD_COMPLETE, remotePath);
        } catch (SftpException e) {
            throw new SFTPException("Upload failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Change file permissions
     */
    public void chmod(String path, int permissions) throws SFTPException {
        checkChannel();
        try {
            channel.chmod(permissions, path);
            logger.debug("Changed permissions: {} -> {}", path, Integer.toOctalString(permissions));
        } catch (SftpException e) {
            throw new SFTPException("Failed to change permissions: " + e.getMessage(), e);
        }
    }
    
    private void checkChannel() throws SFTPException {
        if (channel == null || !channel.isConnected()) {
            throw new SFTPException("SFTP channel not connected");
        }
    }
    
    // Event management
    public void addListener(SFTPEventListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(SFTPEventListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(SFTPEvent event, String path) {
        for (SFTPEventListener listener : listeners) {
            try {
                listener.onSFTPEvent(event, path);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
    
    // Getters
    public boolean isConnected() {
        return channel != null && channel.isConnected();
    }
    
    public String getCurrentRemotePath() {
        return currentRemotePath;
    }
    
    public SSHSession getSSHSession() {
        return sshSession;
    }
    
    public ChannelSftp getChannel() {
        return channel;
    }
    
    /**
     * Progress monitor adapter
     */
    private static class ProgressMonitorAdapter implements SftpProgressMonitor {
        private final TransferProgressListener listener;
        private long total;
        private long transferred;
        
        public ProgressMonitorAdapter(TransferProgressListener listener) {
            this.listener = listener;
        }
        
        @Override
        public void init(int op, String src, String dest, long max) {
            this.total = max;
            this.transferred = 0;
            listener.onStart(src, dest, max);
        }
        
        @Override
        public boolean count(long count) {
            transferred += count;
            listener.onProgress(transferred, total);
            return true;
        }
        
        @Override
        public void end() {
            listener.onComplete();
        }
    }
    
    /**
     * SFTP Events
     */
    public enum SFTPEvent {
        UPLOAD_START,
        UPLOAD_COMPLETE,
        UPLOAD_ERROR,
        DOWNLOAD_START,
        DOWNLOAD_COMPLETE,
        DOWNLOAD_ERROR,
        DELETE,
        MKDIR,
        RENAME
    }
    
    /**
     * SFTP Event Listener
     */
    public interface SFTPEventListener {
        void onSFTPEvent(SFTPEvent event, String path);
    }
    
    /**
     * Transfer Progress Listener
     */
    public interface TransferProgressListener {
        void onStart(String src, String dest, long total);
        void onProgress(long transferred, long total);
        void onComplete();
    }
}
