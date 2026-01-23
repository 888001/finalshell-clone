package com.finalshell.script;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.HashMap;

/**
 * 脚本执行器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ExecuteScript {
    
    private String scriptPath;
    private Map<String, Object> variables;
    private ScriptListener listener;
    
    public ExecuteScript() {
        this.variables = new HashMap<>();
    }
    
    public ExecuteScript(String scriptPath) {
        this();
        this.scriptPath = scriptPath;
    }
    
    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }
    
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    public Object getVariable(String name) {
        return variables.get(name);
    }
    
    public void setListener(ScriptListener listener) {
        this.listener = listener;
    }
    
    public void execute() throws Exception {
        if (scriptPath == null || scriptPath.isEmpty()) {
            throw new IllegalArgumentException("Script path not set");
        }
        
        File file = new File(scriptPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Script file not found: " + scriptPath);
        }
        
        if (listener != null) {
            listener.onStart(scriptPath);
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                executeLine(line, lineNum);
            }
            
            if (listener != null) {
                listener.onComplete(scriptPath);
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(scriptPath, e);
            }
            throw e;
        }
    }
    
    private void executeLine(String line, int lineNum) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return;
        }
        
        if (listener != null) {
            listener.onLine(lineNum, line);
        }
    }
    
    public interface ScriptListener {
        void onStart(String scriptPath);
        void onLine(int lineNum, String line);
        void onComplete(String scriptPath);
        void onError(String scriptPath, Exception e);
    }
}
