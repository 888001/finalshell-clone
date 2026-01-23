package com.finalshell.monitor.parser;

/**
 * Linux命令解析器基类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Monitor_DeepAnalysis.md
 */
public abstract class BaseParser {
    
    protected String rawOutput;
    
    public void setRawOutput(String output) {
        this.rawOutput = output;
    }
    
    public abstract void parse();
    
    protected String[] splitLines(String text) {
        if (text == null) return new String[0];
        return text.split("\\r?\\n");
    }
    
    protected String[] splitWhitespace(String line) {
        if (line == null) return new String[0];
        return line.trim().split("\\s+");
    }
    
    protected long parseLong(String s, long defaultValue) {
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    protected double parseDouble(String s, double defaultValue) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    protected int parseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
