package com.finalshell.batch;

import com.finalshell.config.ConnectConfig;
import com.finalshell.ssh.SSHSession;
import com.finalshell.ssh.SSHException;
import com.jcraft.jsch.ChannelExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 批量命令执行器
 */
public class BatchExecutor {
    private static final Logger logger = LoggerFactory.getLogger(BatchExecutor.class);
    
    private final ExecutorService executor;
    private final List<BatchTask> tasks = new CopyOnWriteArrayList<>();
    private final List<BatchListener> listeners = new CopyOnWriteArrayList<>();
    
    private int maxConcurrent = 5;
    private int commandTimeout = 300; // 秒
    private volatile boolean cancelled = false;
    
    public BatchExecutor() {
        this.executor = Executors.newFixedThreadPool(maxConcurrent);
    }
    
    public BatchExecutor(int maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
        this.executor = Executors.newFixedThreadPool(maxConcurrent);
    }
    
    /**
     * 添加批量任务
     */
    public void addTask(ConnectConfig connection, String command) {
        BatchTask task = new BatchTask(connection, command);
        tasks.add(task);
        notifyTaskAdded(task);
    }
    
    /**
     * 批量添加任务
     */
    public void addTasks(List<ConnectConfig> connections, String command) {
        for (ConnectConfig conn : connections) {
            addTask(conn, command);
        }
    }
    
    /**
     * 执行所有任务
     */
    public void executeAll() {
        cancelled = false;
        notifyBatchStart(tasks.size());
        
        List<Future<?>> futures = new ArrayList<>();
        
        for (BatchTask task : tasks) {
            if (cancelled) break;
            
            Future<?> future = executor.submit(() -> executeTask(task));
            futures.add(future);
        }
        
        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("任务执行异常", e);
            }
        }
        
        notifyBatchComplete(getSuccessCount(), getFailedCount());
    }
    
    /**
     * 异步执行所有任务
     */
    public void executeAllAsync(Runnable onComplete) {
        new Thread(() -> {
            executeAll();
            if (onComplete != null) {
                onComplete.run();
            }
        }).start();
    }
    
    private void executeTask(BatchTask task) {
        if (cancelled) {
            task.setStatus(BatchTask.BatchTaskStatus.CANCELLED);
            notifyTaskUpdate(task);
            return;
        }
        
        task.setStatus(BatchTask.BatchTaskStatus.RUNNING);
        task.setStartTime(System.currentTimeMillis());
        notifyTaskUpdate(task);
        
        SSHSession session = null;
        ChannelExec channel = null;
        
        try {
            // 创建SSH连接
            session = new SSHSession(task.getConnection());
            session.connect();
            
            // 执行命令
            channel = (ChannelExec) session.getSession().openChannel("exec");
            channel.setCommand(task.getCommand());
            channel.setInputStream(null);
            
            InputStream stdout = channel.getInputStream();
            InputStream stderr = channel.getErrStream();
            
            channel.connect(commandTimeout * 1000);
            
            // 读取输出
            StringBuilder outputBuilder = new StringBuilder();
            StringBuilder errorBuilder = new StringBuilder();
            
            ExecutorService readExecutor = Executors.newFixedThreadPool(2);
            
            Future<String> stdoutFuture = readExecutor.submit(() -> readStream(stdout));
            Future<String> stderrFuture = readExecutor.submit(() -> readStream(stderr));
            
            try {
                outputBuilder.append(stdoutFuture.get(commandTimeout, TimeUnit.SECONDS));
                errorBuilder.append(stderrFuture.get(commandTimeout, TimeUnit.SECONDS));
            } catch (TimeoutException e) {
                task.setStatus(BatchTask.BatchTaskStatus.TIMEOUT);
                task.setError("命令执行超时");
                notifyTaskUpdate(task);
                return;
            } finally {
                readExecutor.shutdown();
            }
            
            // 等待命令完成
            while (!channel.isClosed()) {
                Thread.sleep(100);
            }
            
            task.setOutput(outputBuilder.toString());
            task.setError(errorBuilder.toString());
            task.setExitCode(channel.getExitStatus());
            
            if (channel.getExitStatus() == 0) {
                task.setStatus(BatchTask.BatchTaskStatus.SUCCESS);
            } else {
                task.setStatus(BatchTask.BatchTaskStatus.FAILED);
            }
            
        } catch (SSHException e) {
            logger.error("SSH连接失败: {}", task.getHostDisplay(), e);
            task.setStatus(BatchTask.BatchTaskStatus.FAILED);
            task.setError("SSH连接失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("命令执行失败: {}", task.getHostDisplay(), e);
            task.setStatus(BatchTask.BatchTaskStatus.FAILED);
            task.setError("执行失败: " + e.getMessage());
        } finally {
            task.setEndTime(System.currentTimeMillis());
            
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            
            notifyTaskUpdate(task);
        }
    }
    
    private String readStream(InputStream stream) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            logger.debug("读取流异常", e);
        }
        return sb.toString();
    }
    
    /**
     * 取消执行
     */
    public void cancel() {
        cancelled = true;
        for (BatchTask task : tasks) {
            if (task.getStatus() == BatchTask.BatchTaskStatus.PENDING) {
                task.setStatus(BatchTask.BatchTaskStatus.CANCELLED);
            }
        }
    }
    
    /**
     * 清除所有任务
     */
    public void clear() {
        tasks.clear();
    }
    
    /**
     * 获取任务列表
     */
    public List<BatchTask> getTasks() {
        return new ArrayList<>(tasks);
    }
    
    /**
     * 获取成功数量
     */
    public int getSuccessCount() {
        return (int) tasks.stream()
            .filter(t -> t.getStatus() == BatchTask.BatchTaskStatus.SUCCESS)
            .count();
    }
    
    /**
     * 获取失败数量
     */
    public int getFailedCount() {
        return (int) tasks.stream()
            .filter(t -> t.getStatus() == BatchTask.BatchTaskStatus.FAILED || 
                        t.getStatus() == BatchTask.BatchTaskStatus.TIMEOUT)
            .count();
    }
    
    /**
     * 设置命令超时时间
     */
    public void setCommandTimeout(int seconds) {
        this.commandTimeout = seconds;
    }
    
    /**
     * 添加监听器
     */
    public void addListener(BatchListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(BatchListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyTaskAdded(BatchTask task) {
        for (BatchListener l : listeners) {
            l.onTaskAdded(task);
        }
    }
    
    private void notifyTaskUpdate(BatchTask task) {
        for (BatchListener l : listeners) {
            l.onTaskUpdate(task);
        }
    }
    
    private void notifyBatchStart(int totalTasks) {
        for (BatchListener l : listeners) {
            l.onBatchStart(totalTasks);
        }
    }
    
    private void notifyBatchComplete(int success, int failed) {
        for (BatchListener l : listeners) {
            l.onBatchComplete(success, failed);
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    /**
     * 批量执行监听器
     */
    public interface BatchListener {
        void onTaskAdded(BatchTask task);
        void onTaskUpdate(BatchTask task);
        void onBatchStart(int totalTasks);
        void onBatchComplete(int success, int failed);
    }
}
