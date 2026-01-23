package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 表格内嵌面板变体2
 * 带进度条的面板
 */
public class PanelInTable2 extends PanelInTable {
    
    private JProgressBar progressBar;
    
    public PanelInTable2() {
        super();
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 0));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.CENTER);
    }
    
    public void setProgress(int value) {
        progressBar.setValue(value);
    }
    
    public void setProgressString(String text) {
        progressBar.setString(text);
    }
}
