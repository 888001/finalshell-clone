package com.finalshell.process;

import com.finalshell.ssh.SSHSession;
import com.jcraft.jsch.ChannelExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 远程进程管理器
 */
public class ProcessManager {
    private static final Logger logger = LoggerFactory.getLogger(ProcessManager.class);
    
    private final SSHSession session;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<ProcessListener> listeners = new ArrayList<>();
    
    private volatile boolean refreshing = false;
    
    public ProcessManager(SSHSession session) {
        this.session = session;
    }
    
    /**
     * 获取进程列表
     */
    public List<ProcessInfo> getProcessList() throws Exception {
        String command = "ps aux --sort=-%cpu 2>/dev/null || ps -ef";
        String output = executeCommand(command);
        return parseProcessList(output);
    }
    
    /**
     * 异步获取进程列表
     */
    public void getProcessListAsync(ProcessListCallback callback) {
        if (refreshing) return;
        
        refreshing = true;
        executor.submit(() -> {
            try {
                List<ProcessInfo> processes = getProcessList();
                if (callback != null) {
                    callback.onSuccess(processes);
                }
            } catch (Exception e) {
                logger.error("获取进程列表失败", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            } finally {
                refreshing = false;
            }
        });
    }
    
    /**
     * 杀死进程 (with force option)
     */
    public boolean killProcess(int pid, boolean force) {
        try {
            return killProcess(pid, force ? 9 : 15);
        } catch (Exception e) {
            logger.error("结束进程失败: {}", pid, e);
            return false;
        }
    }
    
    /**
     * 杀死进程
     */
    public boolean killProcess(int pid, int signal) throws Exception {
        String command = String.format("kill -%d %d 2>&1", signal, pid);
        String output = executeCommand(command);
        
        boolean success = output.isEmpty() || !output.toLowerCase().contains("error");
        
        if (success) {
            logger.info("进程 {} 已发送信号 {}", pid, signal);
            for (ProcessListener l : listeners) {
                l.onProcessKilled(pid, signal);
            }
        }
        
        return success;
    }
    
    /**
     * 杀死进程 (SIGTERM)
     */
    public boolean killProcess(int pid) throws Exception {
        return killProcess(pid, 15);
    }
    
    /**
     * 强制杀死进程 (SIGKILL)
     */
    public boolean forceKillProcess(int pid) throws Exception {
        return killProcess(pid, 9);
    }
    
    /**
     * 搜索进程
     */
    public List<ProcessInfo> searchProcess(String keyword) throws Exception {
        List<ProcessInfo> all = getProcessList();
        List<ProcessInfo> result = new ArrayList<>();
        
        String lowerKeyword = keyword.toLowerCase();
        for (ProcessInfo p : all) {
            if (p.getCommand().toLowerCase().contains(lowerKeyword) ||
                p.getUser().toLowerCase().contains(lowerKeyword) ||
                String.valueOf(p.getPid()).contains(keyword)) {
                result.add(p);
            }
        }
        
        return result;
    }
    
    /**
     * 获取进程详情
     */
    public String getProcessDetail(int pid) throws Exception {
        StringBuilder sb = new StringBuilder();
        
        // 基本信息
        String cmd = String.format("cat /proc/%d/status 2>/dev/null", pid);
        sb.append("=== 进程状态 ===\n");
        sb.append(executeCommand(cmd));
        
        // 命令行
        cmd = String.format("cat /proc/%d/cmdline 2>/dev/null | tr '\\0' ' '", pid);
        sb.append("\n=== 命令行 ===\n");
        sb.append(executeCommand(cmd));
        
        // 环境变量 (部分)
        cmd = String.format("cat /proc/%d/environ 2>/dev/null | tr '\\0' '\\n' | head -20", pid);
        sb.append("\n=== 环境变量 (前20个) ===\n");
        sb.append(executeCommand(cmd));
        
        // 打开的文件描述符
        cmd = String.format("ls -la /proc/%d/fd 2>/dev/null | head -30", pid);
        sb.append("\n=== 文件描述符 (前30个) ===\n");
        sb.append(executeCommand(cmd));
        
        return sb.toString();
    }
    
    private List<ProcessInfo> parseProcessList(String output) {
        List<ProcessInfo> processes = new ArrayList<>();
        String[] lines = output.split("\n");
        
        // 检测输出格式
        boolean isAux = lines.length > 0 && lines[0].contains("USER");
        
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            
            try {
                ProcessInfo info = isAux ? parseAuxLine(line) : parseEfLine(line);
                if (info != null) {
                    processes.add(info);
                }
            } catch (Exception e) {
                logger.debug("解析进程行失败: {}", line);
            }
        }
        
        return processes;
    }
    
    private ProcessInfo parseAuxLine(String line) {
        // USER PID %CPU %MEM VSZ RSS TTY STAT START TIME COMMAND
        Pattern pattern = Pattern.compile(
            "(\\S+)\\s+(\\d+)\\s+([\\d.]+)\\s+([\\d.]+)\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(.+)"
        );
        
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            ProcessInfo info = new ProcessInfo();
            info.setUser(matcher.group(1));
            info.setPid(Integer.parseInt(matcher.group(2)));
            info.setCpuPercent(Double.parseDouble(matcher.group(3)));
            info.setMemPercent(Double.parseDouble(matcher.group(4)));
            info.setVsz(Long.parseLong(matcher.group(5)));
            info.setRss(Long.parseLong(matcher.group(6)));
            info.setTty(matcher.group(7));
            info.setStat(matcher.group(8));
            info.setStartTime(matcher.group(9));
            info.setTime(matcher.group(10));
            info.setCommand(matcher.group(11));
            return info;
        }
        
        return null;
    }
    
    private ProcessInfo parseEfLine(String line) {
        // UID PID PPID C STIME TTY TIME CMD
        String[] parts = line.split("\\s+", 8);
        if (parts.length >= 8) {
            ProcessInfo info = new ProcessInfo();
            info.setUser(parts[0]);
            info.setPid(Integer.parseInt(parts[1]));
            info.setCpuPercent(0);
            info.setMemPercent(0);
            info.setTty(parts[5]);
            info.setTime(parts[6]);
            info.setCommand(parts[7]);
            return info;
        }
        return null;
    }
    
    private String executeCommand(String command) throws Exception {
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.getSession().openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            
            InputStream in = channel.getInputStream();
            channel.connect(10000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            
            return sb.toString();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
    }
    
    public void addListener(ProcessListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(ProcessListener listener) {
        listeners.remove(listener);
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    /**
     * 进程列表回调
     */
    public interface ProcessListCallback {
        void onSuccess(List<ProcessInfo> processes);
        void onError(String error);
    }
    
    /**
     * 进程监听器
     */
    public interface ProcessListener {
        void onProcessKilled(int pid, int signal);
    }
}
