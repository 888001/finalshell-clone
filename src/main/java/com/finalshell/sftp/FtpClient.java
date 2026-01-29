package com.finalshell.sftp;

import com.finalshell.config.ConnectConfig;
import com.finalshell.ssh.SSHSession;

import com.jcraft.jsch.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * SFTP客户端封装
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SFTP_Transfer_Analysis.md - FtpClient
 */
public class FtpClient {
    
    public static final int TYPE_SSH = 100;
    public static final int TYPE_RDP = 101;
    
    private ConnectConfig connectConfig;
    private Session session;
    private ChannelSftp mainChannel;
    private LinkedBlockingQueue<ChannelSftp> channelPool;
    private int poolSize = 5;
    private boolean closed = false;
    private List<FtpEventListener> listeners = new ArrayList<>();
    
    public FtpClient(ConnectConfig config) {
        this.connectConfig = config;
        this.channelPool = new LinkedBlockingQueue<>(poolSize);
    }
    
    public void connect() throws JSchException {
        if (session != null && session.isConnected()) {
            return;
        }
        
        JSch jsch = new JSch();
        
        // 设置密钥认证
        String keyPath = connectConfig.getKeyPath();
        if (keyPath != null && !keyPath.isEmpty()) {
            String passphrase = connectConfig.getKeyPassphrase();
            if (passphrase != null && !passphrase.isEmpty()) {
                jsch.addIdentity(keyPath, passphrase);
            } else {
                jsch.addIdentity(keyPath);
            }
        }
        
        session = jsch.getSession(
            connectConfig.getUser(),
            connectConfig.getHost(),
            connectConfig.getPort()
        );
        
        // 设置密码
        String password = connectConfig.getPassword();
        if (password != null && !password.isEmpty()) {
            session.setPassword(password);
        }
        
        // 跳过主机密钥检查
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        
        session.connect(30000);
        
        // 打开SFTP通道
        mainChannel = openChannel();
    }
    
    private ChannelSftp openChannel() throws JSchException {
        Channel channel = session.openChannel("sftp");
        channel.connect(30000);
        return (ChannelSftp) channel;
    }
    
    public ChannelSftp getChannel() throws JSchException, InterruptedException {
        ChannelSftp channel = channelPool.poll();
        if (channel == null || !channel.isConnected()) {
            channel = openChannel();
        }
        return channel;
    }
    
    public void returnChannel(ChannelSftp channel) {
        if (channel != null && channel.isConnected()) {
            if (!channelPool.offer(channel)) {
                channel.disconnect();
            }
        }
    }
    
    public List<RemoteFile> listFiles(String path) throws SftpException {
        List<RemoteFile> files = new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        Vector<ChannelSftp.LsEntry> entries = mainChannel.ls(path);
        
        for (ChannelSftp.LsEntry entry : entries) {
            String name = entry.getFilename();
            if (".".equals(name) || "..".equals(name)) {
                continue;
            }
            
            SftpATTRS attrs = entry.getAttrs();
            RemoteFile file = new RemoteFile();
            file.setName(name);
            file.setPath(path);
            file.setSize(attrs.getSize());
            file.setDirectory(attrs.isDir());
            file.setLink(attrs.isLink());
            file.setMtime(attrs.getMTime() * 1000L);
            file.setPermissions(attrs.getPermissionsString());
            
            files.add(file);
        }
        
        return files;
    }
    
    public void download(String remotePath, String localPath, SftpProgressMonitor monitor) 
            throws SftpException, JSchException, InterruptedException {
        ChannelSftp channel = getChannel();
        try {
            channel.get(remotePath, localPath, monitor);
        } finally {
            returnChannel(channel);
        }
    }
    
    public void upload(String localPath, String remotePath, SftpProgressMonitor monitor) 
            throws SftpException, JSchException, InterruptedException {
        ChannelSftp channel = getChannel();
        try {
            channel.put(localPath, remotePath, monitor);
        } finally {
            returnChannel(channel);
        }
    }
    
    public void upload(String localPath, String remotePath) 
            throws SftpException, JSchException, InterruptedException {
        upload(localPath, remotePath, null);
    }
    
    public void download(String remotePath, String localPath) 
            throws SftpException, JSchException, InterruptedException {
        download(remotePath, localPath, null);
    }
    
    public void mkdir(String path) throws SftpException {
        mainChannel.mkdir(path);
    }
    
    public void rmdir(String path) throws SftpException {
        mainChannel.rmdir(path);
    }
    
    public void rm(String path) throws SftpException {
        mainChannel.rm(path);
    }
    
    public void rename(String oldPath, String newPath) throws SftpException {
        mainChannel.rename(oldPath, newPath);
    }
    
    public void chmod(int permissions, String path) throws SftpException {
        mainChannel.chmod(permissions, path);
    }
    
    public String pwd() throws SftpException {
        return mainChannel.pwd();
    }
    
    public void cd(String path) throws SftpException {
        mainChannel.cd(path);
    }
    
    public void disconnect() {
        closed = true;
        
        // 关闭池中的通道
        ChannelSftp channel;
        while ((channel = channelPool.poll()) != null) {
            channel.disconnect();
        }
        
        if (mainChannel != null) {
            mainChannel.disconnect();
        }
        
        if (session != null) {
            session.disconnect();
        }
    }
    
    public boolean isConnected() {
        return session != null && session.isConnected() && !closed;
    }
    
    public void addListener(FtpEventListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(FtpEventListener listener) {
        listeners.remove(listener);
    }
    
    public ConnectConfig getConnectConfig() {
        return connectConfig;
    }
    
    public Session getSession() {
        return session;
    }
}
