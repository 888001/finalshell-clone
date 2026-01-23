package com.finalshell.search;

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
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 远程文件搜索器
 */
public class FileSearcher {
    private static final Logger logger = LoggerFactory.getLogger(FileSearcher.class);
    
    private final SSHSession session;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private Future<?> currentTask;
    
    public FileSearcher(SSHSession session) {
        this.session = session;
    }
    
    /**
     * 按文件名搜索
     */
    public void searchByName(String directory, String pattern, boolean caseSensitive,
                            boolean includeHidden, int maxResults, SearchCallback callback) {
        cancelled.set(false);
        
        currentTask = executor.submit(() -> {
            try {
                callback.onSearchStart();
                
                StringBuilder cmd = new StringBuilder("find ");
                cmd.append(escapeShell(directory));
                
                if (!includeHidden) {
                    cmd.append(" -not -path '*/\\.*'");
                }
                
                if (caseSensitive) {
                    cmd.append(" -name ");
                } else {
                    cmd.append(" -iname ");
                }
                cmd.append("'").append(pattern).append("'");
                
                cmd.append(" -printf '%y|%s|%T+|%u|%M|%p\\n'");
                cmd.append(" 2>/dev/null | head -n ").append(maxResults);
                
                List<FileSearchResult> results = executeSearchCommand(cmd.toString());
                
                if (!cancelled.get()) {
                    callback.onSearchComplete(results);
                }
                
            } catch (Exception e) {
                logger.error("文件搜索失败", e);
                callback.onSearchError(e.getMessage());
            }
        });
    }
    
    /**
     * 按内容搜索 (grep)
     */
    public void searchByContent(String directory, String content, String filePattern,
                               boolean caseSensitive, boolean regex, int maxResults, 
                               SearchCallback callback) {
        cancelled.set(false);
        
        currentTask = executor.submit(() -> {
            try {
                callback.onSearchStart();
                
                StringBuilder cmd = new StringBuilder("grep -rn");
                
                if (!caseSensitive) {
                    cmd.append("i");
                }
                if (!regex) {
                    cmd.append("F");
                }
                
                cmd.append(" ");
                cmd.append(escapeShell(content));
                cmd.append(" ").append(escapeShell(directory));
                
                if (filePattern != null && !filePattern.isEmpty()) {
                    cmd.append(" --include=").append(escapeShell(filePattern));
                }
                
                cmd.append(" 2>/dev/null | head -n ").append(maxResults);
                
                List<FileSearchResult> results = executeGrepCommand(cmd.toString());
                
                if (!cancelled.get()) {
                    callback.onSearchComplete(results);
                }
                
            } catch (Exception e) {
                logger.error("内容搜索失败", e);
                callback.onSearchError(e.getMessage());
            }
        });
    }
    
    /**
     * 按大小搜索
     */
    public void searchBySize(String directory, long minSize, long maxSize, 
                            int maxResults, SearchCallback callback) {
        cancelled.set(false);
        
        currentTask = executor.submit(() -> {
            try {
                callback.onSearchStart();
                
                StringBuilder cmd = new StringBuilder("find ");
                cmd.append(escapeShell(directory));
                cmd.append(" -type f");
                
                if (minSize > 0) {
                    cmd.append(" -size +").append(minSize - 1).append("c");
                }
                if (maxSize > 0) {
                    cmd.append(" -size -").append(maxSize + 1).append("c");
                }
                
                cmd.append(" -printf '%y|%s|%T+|%u|%M|%p\\n'");
                cmd.append(" 2>/dev/null | head -n ").append(maxResults);
                
                List<FileSearchResult> results = executeSearchCommand(cmd.toString());
                
                if (!cancelled.get()) {
                    callback.onSearchComplete(results);
                }
                
            } catch (Exception e) {
                logger.error("大小搜索失败", e);
                callback.onSearchError(e.getMessage());
            }
        });
    }
    
    /**
     * 按修改时间搜索
     */
    public void searchByTime(String directory, int daysAgo, boolean newerThan,
                            int maxResults, SearchCallback callback) {
        cancelled.set(false);
        
        currentTask = executor.submit(() -> {
            try {
                callback.onSearchStart();
                
                StringBuilder cmd = new StringBuilder("find ");
                cmd.append(escapeShell(directory));
                cmd.append(" -type f");
                
                if (newerThan) {
                    cmd.append(" -mtime -").append(daysAgo);
                } else {
                    cmd.append(" -mtime +").append(daysAgo);
                }
                
                cmd.append(" -printf '%y|%s|%T+|%u|%M|%p\\n'");
                cmd.append(" 2>/dev/null | head -n ").append(maxResults);
                
                List<FileSearchResult> results = executeSearchCommand(cmd.toString());
                
                if (!cancelled.get()) {
                    callback.onSearchComplete(results);
                }
                
            } catch (Exception e) {
                logger.error("时间搜索失败", e);
                callback.onSearchError(e.getMessage());
            }
        });
    }
    
    private List<FileSearchResult> executeSearchCommand(String command) throws Exception {
        List<FileSearchResult> results = new ArrayList<>();
        
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.getSession().openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            
            InputStream in = channel.getInputStream();
            channel.connect(30000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            
            while ((line = reader.readLine()) != null && !cancelled.get()) {
                FileSearchResult result = parseFindLine(line);
                if (result != null) {
                    results.add(result);
                }
            }
            
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
        
        return results;
    }
    
    private List<FileSearchResult> executeGrepCommand(String command) throws Exception {
        List<FileSearchResult> results = new ArrayList<>();
        
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.getSession().openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            
            InputStream in = channel.getInputStream();
            channel.connect(30000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            
            // grep输出格式: 文件名:行号:内容
            Pattern pattern = Pattern.compile("^(.+?):(\\d+):(.*)$");
            
            while ((line = reader.readLine()) != null && !cancelled.get()) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    FileSearchResult result = new FileSearchResult();
                    result.setPath(matcher.group(1));
                    result.setName(getFileName(matcher.group(1)));
                    result.setLineNumber(Integer.parseInt(matcher.group(2)));
                    result.setMatchLine(matcher.group(3).trim());
                    results.add(result);
                }
            }
            
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
        
        return results;
    }
    
    private FileSearchResult parseFindLine(String line) {
        // 格式: type|size|time|owner|mode|path
        String[] parts = line.split("\\|", 6);
        if (parts.length >= 6) {
            FileSearchResult result = new FileSearchResult();
            result.setDirectory("d".equals(parts[0]));
            try {
                result.setSize(Long.parseLong(parts[1]));
            } catch (NumberFormatException e) {
                result.setSize(0);
            }
            result.setModifyTime(parts[2]);
            result.setOwner(parts[3]);
            result.setPermissions(parts[4]);
            result.setPath(parts[5]);
            result.setName(getFileName(parts[5]));
            return result;
        }
        return null;
    }
    
    private String getFileName(String path) {
        int idx = path.lastIndexOf('/');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }
    
    private String escapeShell(String s) {
        return "'" + s.replace("'", "'\\''") + "'";
    }
    
    public void cancel() {
        cancelled.set(true);
        if (currentTask != null) {
            currentTask.cancel(true);
        }
    }
    
    public void shutdown() {
        cancel();
        executor.shutdown();
    }
    
    /**
     * 搜索回调
     */
    public interface SearchCallback {
        void onSearchStart();
        void onSearchComplete(List<FileSearchResult> results);
        void onSearchError(String error);
    }
}
