package com.finalshell.script;

import com.finalshell.ssh.SSHSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * JavaScript脚本引擎
 */
public class ScriptEngine {
    private static final Logger logger = LoggerFactory.getLogger(ScriptEngine.class);
    
    private final javax.script.ScriptEngine jsEngine;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ScriptContext context;
    
    private SSHSession session;
    private ScriptCallback callback;
    private volatile boolean running = false;
    private Future<?> currentTask;
    
    public ScriptEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        jsEngine = manager.getEngineByName("nashorn");
        if (jsEngine == null) {
            throw new RuntimeException("JavaScript引擎不可用");
        }
        context = jsEngine.getContext();
    }
    
    /**
     * 设置SSH会话
     */
    public void setSession(SSHSession session) {
        this.session = session;
        // 注入API
        jsEngine.put("ssh", new ScriptSSHAPI(session, this));
    }
    
    /**
     * 设置回调
     */
    public void setCallback(ScriptCallback callback) {
        this.callback = callback;
    }
    
    /**
     * 执行脚本
     */
    public void execute(String script) {
        if (running) {
            if (callback != null) {
                callback.onError("脚本正在运行中");
            }
            return;
        }
        
        running = true;
        currentTask = executor.submit(() -> {
            try {
                if (callback != null) {
                    callback.onStart();
                }
                
                // 注入常用API
                injectAPIs();
                
                Object result = jsEngine.eval(script);
                
                if (callback != null) {
                    callback.onComplete(result);
                }
                
            } catch (ScriptException e) {
                logger.error("脚本执行错误", e);
                if (callback != null) {
                    callback.onError("行 " + e.getLineNumber() + ": " + e.getMessage());
                }
            } catch (Exception e) {
                logger.error("脚本执行错误", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            } finally {
                running = false;
            }
        });
    }
    
    /**
     * 执行脚本文件
     */
    public void executeFile(File scriptFile) {
        try {
            String script = readFile(scriptFile);
            execute(script);
        } catch (IOException e) {
            if (callback != null) {
                callback.onError("读取脚本文件失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 停止脚本
     */
    public void stop() {
        if (currentTask != null) {
            currentTask.cancel(true);
        }
        running = false;
    }
    
    /**
     * 注入常用API
     */
    private void injectAPIs() {
        // 打印函数
        jsEngine.put("print", (PrintFunction) this::print);
        jsEngine.put("println", (PrintFunction) s -> print(s + "\n"));
        
        // 延时函数
        jsEngine.put("sleep", (SleepFunction) this::sleep);
        
        // 工具函数
        jsEngine.put("utils", new ScriptUtils());
    }
    
    private void print(String text) {
        if (callback != null) {
            callback.onOutput(text);
        }
    }
    
    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public void shutdown() {
        stop();
        executor.shutdown();
    }
    
    // 函数接口
    @FunctionalInterface
    public interface PrintFunction {
        void print(String text);
    }
    
    @FunctionalInterface
    public interface SleepFunction {
        void sleep(int ms);
    }
    
    /**
     * 脚本回调
     */
    public interface ScriptCallback {
        void onStart();
        void onOutput(String text);
        void onComplete(Object result);
        void onError(String error);
    }
    
    /**
     * SSH API (供脚本调用)
     */
    public static class ScriptSSHAPI {
        private final SSHSession session;
        private final ScriptEngine engine;
        
        public ScriptSSHAPI(SSHSession session, ScriptEngine engine) {
            this.session = session;
            this.engine = engine;
        }
        
        public String exec(String command) {
            if (session == null || !session.isConnected()) {
                return "ERROR: SSH未连接";
            }
            
            try {
                com.jcraft.jsch.ChannelExec channel = 
                    (com.jcraft.jsch.ChannelExec) session.getSession().openChannel("exec");
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
                
                channel.disconnect();
                return sb.toString();
                
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }
        
        public boolean isConnected() {
            return session != null && session.isConnected();
        }
    }
    
    /**
     * 工具类 (供脚本调用)
     */
    public static class ScriptUtils {
        public String timestamp() {
            return String.valueOf(System.currentTimeMillis());
        }
        
        public String date() {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        }
        
        public String readFile(String path) throws IOException {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString();
        }
        
        public void writeFile(String path, String content) throws IOException {
            try (FileWriter writer = new FileWriter(path)) {
                writer.write(content);
            }
        }
    }
}
