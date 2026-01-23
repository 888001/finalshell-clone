package com.finalshell.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.Bindings;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;

/**
 * JavaScript测试引擎
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class JSTestEngine {
    
    private ScriptEngine engine;
    private Map<String, Object> globalBindings;
    
    public JSTestEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
        globalBindings = new HashMap<>();
    }
    
    public void setBinding(String name, Object value) {
        globalBindings.put(name, value);
    }
    
    public Object eval(String script) throws ScriptException {
        if (engine == null) {
            throw new ScriptException("JavaScript engine not available");
        }
        
        Bindings bindings = engine.createBindings();
        bindings.putAll(globalBindings);
        
        return engine.eval(script, bindings);
    }
    
    public Object eval(Reader reader) throws ScriptException {
        if (engine == null) {
            throw new ScriptException("JavaScript engine not available");
        }
        
        Bindings bindings = engine.createBindings();
        bindings.putAll(globalBindings);
        
        return engine.eval(reader, bindings);
    }
    
    public boolean test(String expression) throws ScriptException {
        Object result = eval(expression);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return result != null;
    }
    
    public boolean isAvailable() {
        return engine != null;
    }
    
    public String getEngineName() {
        if (engine != null) {
            return engine.getFactory().getEngineName();
        }
        return "N/A";
    }
}
