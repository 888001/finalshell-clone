package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;

/**
 * 行面板
 * 用于显示监控数据的行
 */
public class RowPanel extends JPanel {
    
    private JLabel nameLabel;
    private JLabel valueLabel;
    private JProgressBar progressBar;
    private boolean showProgress = false;
    
    public RowPanel() {
        this("", "");
    }
    
    public RowPanel(String name, String value) {
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);
        
        nameLabel = new JLabel(name);
        nameLabel.setPreferredSize(new Dimension(100, 20));
        add(nameLabel, BorderLayout.WEST);
        
        valueLabel = new JLabel(value);
        add(valueLabel, BorderLayout.CENTER);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(100, 16));
        progressBar.setVisible(false);
        add(progressBar, BorderLayout.EAST);
    }
    
    public void setName(String name) {
        nameLabel.setText(name);
    }
    
    public void setValue(String value) {
        valueLabel.setText(value);
    }
    
    public void setProgress(int value) {
        progressBar.setValue(value);
        if (showProgress && !progressBar.isVisible()) {
            progressBar.setVisible(true);
        }
    }
    
    public void setShowProgress(boolean show) {
        this.showProgress = show;
        progressBar.setVisible(show);
    }
    
    public void setNameWidth(int width) {
        nameLabel.setPreferredSize(new Dimension(width, 20));
    }
}
