package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 浮动导航对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FloatNav_Panel_DeepAnalysis.md - FloatNavDialog
 */
public class FloatNavDialog extends JDialog {
    
    private long lastLostFocusTime;
    private FloatNavPanel navPanel;
    
    public FloatNavDialog(Window owner) {
        super(owner);
        
        this.navPanel = new FloatNavPanel(this);
        this.setUndecorated(true);
        this.setContentPane(navPanel);
        
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {}
            
            @Override
            public void windowLostFocus(WindowEvent e) {
                lastLostFocusTime = System.currentTimeMillis();
                setVisible(false);
            }
        });
        
        setSize(320, 320);
    }
    
    /**
     * 显示在指定位置
     */
    public void showAt(int x, int y) {
        setLocation(x, y);
        setVisible(true);
        toFront();
    }
    
    /**
     * 显示在组件下方
     */
    public void showBelow(Component comp) {
        Point loc = comp.getLocationOnScreen();
        showAt(loc.x, loc.y + comp.getHeight());
    }
    
    public long getLastLostFocusTime() { return lastLostFocusTime; }
    public FloatNavPanel getNavPanel() { return navPanel; }
}
