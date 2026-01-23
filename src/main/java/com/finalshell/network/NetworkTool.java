package com.finalshell.network;

import com.finalshell.ssh.SSHSession;
import com.jcraft.jsch.ChannelExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 网络工具类
 */
public class NetworkTool {
    private static final Logger logger = LoggerFactory.getLogger(NetworkTool.class);
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    
    /**
     * 本地Ping
     */
    public void ping(String host, int count, PingCallback callback) {
        cancelled.set(false);
        
        executor.submit(() -> {
            try {
                callback.onStart();
                
                for (int i = 0; i < count && !cancelled.get(); i++) {
                    long start = System.currentTimeMillis();
                    InetAddress address = InetAddress.getByName(host);
                    boolean reachable = address.isReachable(5000);
                    long time = System.currentTimeMillis() - start;
                    
                    if (reachable) {
                        callback.onReply(host, i + 1, time);
                    } else {
                        callback.onTimeout(host, i + 1);
                    }
                    
                    if (i < count - 1 && !cancelled.get()) {
                        Thread.sleep(1000);
                    }
                }
                
                callback.onComplete();
                
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
    
    /**
     * 远程Ping (通过SSH)
     */
    public void remotePing(SSHSession session, String host, int count, PingCallback callback) {
        cancelled.set(false);
        
        executor.submit(() -> {
            ChannelExec channel = null;
            try {
                callback.onStart();
                
                String cmd = String.format("ping -c %d %s 2>&1", count, host);
                channel = (ChannelExec) session.getSession().openChannel("exec");
                channel.setCommand(cmd);
                channel.setInputStream(null);
                
                InputStream in = channel.getInputStream();
                channel.connect(10000);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                int seq = 0;
                
                while ((line = reader.readLine()) != null && !cancelled.get()) {
                    callback.onOutput(line);
                    
                    if (line.contains("time=")) {
                        seq++;
                        String timeStr = line.replaceAll(".*time=([\\d.]+).*", "$1");
                        try {
                            long time = (long) Double.parseDouble(timeStr);
                            callback.onReply(host, seq, time);
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                    } else if (line.contains("timeout") || line.contains("unreachable")) {
                        seq++;
                        callback.onTimeout(host, seq);
                    }
                }
                
                callback.onComplete();
                
            } catch (Exception e) {
                callback.onError(e.getMessage());
            } finally {
                if (channel != null && channel.isConnected()) {
                    channel.disconnect();
                }
            }
        });
    }
    
    /**
     * 端口扫描
     */
    public void portScan(String host, int startPort, int endPort, int timeout, PortScanCallback callback) {
        cancelled.set(false);
        
        executor.submit(() -> {
            try {
                callback.onStart(endPort - startPort + 1);
                
                for (int port = startPort; port <= endPort && !cancelled.get(); port++) {
                    boolean open = isPortOpen(host, port, timeout);
                    callback.onPortScanned(port, open);
                }
                
                callback.onComplete();
                
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
    
    /**
     * 检测单个端口
     */
    public boolean isPortOpen(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Traceroute (远程)
     */
    public void traceroute(SSHSession session, String host, TraceCallback callback) {
        cancelled.set(false);
        
        executor.submit(() -> {
            ChannelExec channel = null;
            try {
                callback.onStart();
                
                String cmd = String.format("traceroute -n %s 2>&1 || tracert %s 2>&1", host, host);
                channel = (ChannelExec) session.getSession().openChannel("exec");
                channel.setCommand(cmd);
                channel.setInputStream(null);
                
                InputStream in = channel.getInputStream();
                channel.connect(30000);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                
                while ((line = reader.readLine()) != null && !cancelled.get()) {
                    callback.onHop(line);
                }
                
                callback.onComplete();
                
            } catch (Exception e) {
                callback.onError(e.getMessage());
            } finally {
                if (channel != null && channel.isConnected()) {
                    channel.disconnect();
                }
            }
        });
    }
    
    /**
     * DNS查询
     */
    public void dnsLookup(String host, DnsCallback callback) {
        executor.submit(() -> {
            try {
                InetAddress[] addresses = InetAddress.getAllByName(host);
                String[] ips = new String[addresses.length];
                for (int i = 0; i < addresses.length; i++) {
                    ips[i] = addresses[i].getHostAddress();
                }
                callback.onSuccess(host, ips);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
    
    /**
     * 反向DNS查询
     */
    public void reverseDns(String ip, DnsCallback callback) {
        executor.submit(() -> {
            try {
                InetAddress address = InetAddress.getByName(ip);
                String hostname = address.getCanonicalHostName();
                callback.onSuccess(ip, new String[]{hostname});
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
    
    public void cancel() {
        cancelled.set(true);
    }
    
    public void shutdown() {
        cancel();
        executor.shutdown();
    }
    
    // Callback interfaces
    
    public interface PingCallback {
        void onStart();
        void onReply(String host, int seq, long time);
        void onTimeout(String host, int seq);
        void onOutput(String line);
        void onComplete();
        void onError(String error);
    }
    
    public interface PortScanCallback {
        void onStart(int totalPorts);
        void onPortScanned(int port, boolean open);
        void onComplete();
        void onError(String error);
    }
    
    public interface TraceCallback {
        void onStart();
        void onHop(String line);
        void onComplete();
        void onError(String error);
    }
    
    public interface DnsCallback {
        void onSuccess(String query, String[] results);
        void onError(String error);
    }
}
