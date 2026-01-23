package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 空白占位面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - BlankPanel
 */
public class BlankPanel extends JPanel {
    
    public BlankPanel() {
        setOpaque(false);
    }
    
    public BlankPanel(Color bgColor) {
        setBackground(bgColor);
        setOpaque(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
