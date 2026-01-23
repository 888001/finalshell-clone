package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 全屏对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Font_Menu_FullScreen_UI_DeepAnalysis.md - FullScreenDialog
 */
public class FullScreenDialog extends JDialog {
    
    private JComponent content;
    private boolean fullScreen = true;
    private GraphicsDevice device;
    private FullScreenListener listener;
    
    public FullScreenDialog(Window owner, JComponent content) {
        super(owner);
        this.content = content;
        
        setUndecorated(true);
        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        
        // ESC退出全屏
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitFullScreen");
        getRootPane().getActionMap().put("exitFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitFullScreen();
            }
        });
        
        // F11切换全屏
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullScreen");
        getRootPane().getActionMap().put("toggleFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitFullScreen();
            }
        });
        
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }
    
    /**
     * 进入全屏
     */
    public void enterFullScreen() {
        if (device.isFullScreenSupported()) {
            device.setFullScreenWindow(this);
        } else {
            // 模拟全屏
            Rectangle bounds = device.getDefaultConfiguration().getBounds();
            setBounds(bounds);
        }
        fullScreen = true;
        setVisible(true);
        
        if (listener != null) {
            listener.onFullScreenEnter();
        }
    }
    
    /**
     * 退出全屏
     */
    public void exitFullScreen() {
        if (device.isFullScreenSupported()) {
            device.setFullScreenWindow(null);
        }
        fullScreen = false;
        dispose();
        
        if (listener != null) {
            listener.onFullScreenExit();
        }
    }
    
    public boolean isFullScreen() { return fullScreen; }
    
    public void setListener(FullScreenListener listener) {
        this.listener = listener;
    }
    
    public interface FullScreenListener {
        void onFullScreenEnter();
        void onFullScreenExit();
    }
}
