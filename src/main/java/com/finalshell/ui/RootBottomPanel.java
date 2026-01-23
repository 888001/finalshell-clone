package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 根底部面板（状态栏容器）
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - RootBottomPanel
 */
public class RootBottomPanel extends JPanel {
    
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    public RootBottomPanel() {
        setLayout(new BorderLayout(5, 0));
        setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        setBackground(new Color(240, 240, 240));
        
        statusLabel = new JLabel("就绪");
        add(statusLabel, BorderLayout.WEST);
        
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(150, 15));
        progressBar.setVisible(false);
        add(progressBar, BorderLayout.EAST);
    }
    
    public void setStatus(String text) {
        statusLabel.setText(text);
    }
    
    public void showProgress(boolean show) {
        progressBar.setVisible(show);
    }
    
    public void setProgress(int value) {
        progressBar.setValue(value);
    }
    
    public void setProgressIndeterminate(boolean indeterminate) {
        progressBar.setIndeterminate(indeterminate);
    }
}
