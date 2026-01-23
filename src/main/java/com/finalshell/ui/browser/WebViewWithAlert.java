package com.finalshell.ui.browser;

import javax.swing.*;
import java.awt.*;

/**
 * 带Alert和Confirm的WebView组件
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class WebViewWithAlert extends SwingFXWebView {
    
    private AlertHandler alertHandler;
    private ConfirmHandler confirmHandler;
    private PromptHandler promptHandler;
    
    public WebViewWithAlert() {
        super();
    }
    
    public void setAlertHandler(AlertHandler handler) {
        this.alertHandler = handler;
    }
    
    public void setConfirmHandler(ConfirmHandler handler) {
        this.confirmHandler = handler;
    }
    
    public void setPromptHandler(PromptHandler handler) {
        this.promptHandler = handler;
    }
    
    protected void showAlert(String message) {
        if (alertHandler != null) {
            alertHandler.handleAlert(message);
        } else {
            JOptionPane.showMessageDialog(this, message, "提示", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    protected boolean showConfirm(String message) {
        if (confirmHandler != null) {
            return confirmHandler.handleConfirm(message);
        } else {
            int result = JOptionPane.showConfirmDialog(this, message, "确认", 
                JOptionPane.YES_NO_OPTION);
            return result == JOptionPane.YES_OPTION;
        }
    }
    
    protected String showPrompt(String message, String defaultValue) {
        if (promptHandler != null) {
            return promptHandler.handlePrompt(message, defaultValue);
        } else {
            return JOptionPane.showInputDialog(this, message, defaultValue);
        }
    }
    
    public interface AlertHandler {
        void handleAlert(String message);
    }
    
    public interface ConfirmHandler {
        boolean handleConfirm(String message);
    }
    
    public interface PromptHandler {
        String handlePrompt(String message, String defaultValue);
    }
}
