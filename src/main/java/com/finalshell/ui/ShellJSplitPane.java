package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 自定义分割面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - ShellJSplitPane
 */
public class ShellJSplitPane extends JSplitPane {
    
    private static final int DIVIDER_SIZE = 5;
    
    public ShellJSplitPane() {
        super();
        init();
    }
    
    public ShellJSplitPane(int orientation) {
        super(orientation);
        init();
    }
    
    public ShellJSplitPane(int orientation, Component left, Component right) {
        super(orientation, left, right);
        init();
    }
    
    private void init() {
        setDividerSize(DIVIDER_SIZE);
        setBorder(null);
        setContinuousLayout(true);
    }
    
    @Override
    public void setDividerLocation(double proportionalLocation) {
        super.setDividerLocation(proportionalLocation);
    }
    
    public void setDividerLocationLater(double proportionalLocation) {
        SwingUtilities.invokeLater(() -> {
            setDividerLocation(proportionalLocation);
        });
    }
}
