package com.finalshell.util;

/**
 * 检查结果类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CheckResult {
    
    private boolean success;
    private String message;
    private Object data;
    private int code;
    
    public CheckResult() {
        this.success = false;
    }
    
    public CheckResult(boolean success) {
        this.success = success;
    }
    
    public CheckResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public CheckResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public static CheckResult success() {
        return new CheckResult(true);
    }
    
    public static CheckResult success(String message) {
        return new CheckResult(true, message);
    }
    
    public static CheckResult success(String message, Object data) {
        return new CheckResult(true, message, data);
    }
    
    public static CheckResult fail() {
        return new CheckResult(false);
    }
    
    public static CheckResult fail(String message) {
        return new CheckResult(false, message);
    }
    
    public static CheckResult fail(String message, int code) {
        CheckResult result = new CheckResult(false, message);
        result.setCode(code);
        return result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    @Override
    public String toString() {
        return String.format("CheckResult{success=%s, message='%s', code=%d}", 
            success, message, code);
    }
}
