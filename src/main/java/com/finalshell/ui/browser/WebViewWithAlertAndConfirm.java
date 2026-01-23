package com.finalshell.ui.browser;

import javax.swing.*;

/**
 * 带Alert和Confirm对话框的WebView
 * 扩展WebViewWithAlert，添加confirm对话框支持
 */
public class WebViewWithAlertAndConfirm extends WebViewWithAlert {
    
    private ConfirmHandler confirmHandler;
    private PromptHandler promptHandler;
    
    public WebViewWithAlertAndConfirm() {
        super();
        this.confirmHandler = this::defaultConfirm;
        this.promptHandler = this::defaultPrompt;
    }
    
    public void setConfirmHandler(ConfirmHandler handler) {
        this.confirmHandler = handler;
    }
    
    public void setPromptHandler(PromptHandler handler) {
        this.promptHandler = handler;
    }
    
    public boolean confirm(String message) {
        if (confirmHandler != null) {
            return confirmHandler.handleConfirm(message);
        }
        return defaultConfirm(message);
    }
    
    public String prompt(String message, String defaultValue) {
        if (promptHandler != null) {
            return promptHandler.handlePrompt(message, defaultValue);
        }
        return defaultPrompt(message, defaultValue);
    }
    
    private boolean defaultConfirm(String message) {
        int result = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    private String defaultPrompt(String message, String defaultValue) {
        return JOptionPane.showInputDialog(
                this,
                message,
                defaultValue
        );
    }
    
    public interface ConfirmHandler {
        boolean handleConfirm(String message);
    }
    
    public interface PromptHandler {
        String handlePrompt(String message, String defaultValue);
    }
}
