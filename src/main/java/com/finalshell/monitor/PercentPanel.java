package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;

/**
 * 百分比面板
 * 显示标签和百分比进度条
 */
public class PercentPanel extends JPanel {
    
    private JLabel nameLabel;
    private PercentBar percentBar;
    private JLabel valueLabel;
    
    public PercentPanel() {
        this("");
    }
    
    public PercentPanel(String name) {
        setLayout(new BorderLayout(5, 0));
        setOpaque(false);
        
        nameLabel = new JLabel(name);
        nameLabel.setPreferredSize(new Dimension(80, 20));
        add(nameLabel, BorderLayout.WEST);
        
        percentBar = new PercentBar();
        add(percentBar, BorderLayout.CENTER);
        
        valueLabel = new JLabel("0%");
        valueLabel.setPreferredSize(new Dimension(50, 20));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(valueLabel, BorderLayout.EAST);
    }
    
    public void setName(String name) {
        nameLabel.setText(name);
    }
    
    public void setValue(double value) {
        percentBar.setValue(value);
        valueLabel.setText(String.format("%.1f%%", value));
    }
    
    public void setBarColor(Color color) {
        percentBar.setBarColor(color);
    }
    
    public void setNameWidth(int width) {
        nameLabel.setPreferredSize(new Dimension(width, 20));
    }
}
