package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 全屏面板
 * 支持终端全屏显示，按ESC退出
 */
public class FullScreenPanel extends JPanel {
    
    private JComponent content;
    private Window fullScreenWindow;
    private Container originalParent;
    private int originalIndex;
    private Object originalConstraints;
    private boolean isFullScreen;
    private FullScreenListener listener;
    
    public FullScreenPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && isFullScreen) {
                    exitFullScreen();
                }
            }
        });
        
        setFocusable(true);
    }
    
    public void setContent(JComponent content) {
        this.content = content;
        removeAll();
        if (content != null) {
            add(content, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }
    
    public JComponent getContent() {
        return content;
    }
    
    public void enterFullScreen() {
        if (isFullScreen || content == null) {
            return;
        }
        
        originalParent = content.getParent();
        if (originalParent != null) {
            if (originalParent instanceof JComponent) {
                LayoutManager lm = originalParent.getLayout();
                if (lm instanceof BorderLayout) {
                    originalConstraints = ((BorderLayout) lm).getConstraints(content);
                }
            }
            originalIndex = getComponentIndex(originalParent, content);
            originalParent.remove(content);
        }
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        fullScreenWindow = new JWindow();
        fullScreenWindow.setLayout(new BorderLayout());
        fullScreenWindow.add(content, BorderLayout.CENTER);
        fullScreenWindow.setBackground(Color.BLACK);
        
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(fullScreenWindow);
        } else {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            fullScreenWindow.setBounds(bounds);
            fullScreenWindow.setVisible(true);
        }
        
        isFullScreen = true;
        content.requestFocusInWindow();
        
        if (listener != null) {
            listener.onEnterFullScreen();
        }
    }
    
    public void exitFullScreen() {
        if (!isFullScreen || fullScreenWindow == null) {
            return;
        }
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        if (gd.getFullScreenWindow() == fullScreenWindow) {
            gd.setFullScreenWindow(null);
        }
        
        fullScreenWindow.remove(content);
        fullScreenWindow.dispose();
        fullScreenWindow = null;
        
        if (originalParent != null && content != null) {
            if (originalConstraints != null) {
                originalParent.add(content, originalConstraints, originalIndex);
            } else {
                originalParent.add(content, originalIndex);
            }
            originalParent.revalidate();
            originalParent.repaint();
        }
        
        isFullScreen = false;
        
        if (listener != null) {
            listener.onExitFullScreen();
        }
    }
    
    public void toggleFullScreen() {
        if (isFullScreen) {
            exitFullScreen();
        } else {
            enterFullScreen();
        }
    }
    
    public boolean isFullScreen() {
        return isFullScreen;
    }
    
    public void setFullScreenListener(FullScreenListener listener) {
        this.listener = listener;
    }
    
    private int getComponentIndex(Container parent, Component comp) {
        Component[] components = parent.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == comp) {
                return i;
            }
        }
        return 0;
    }
    
    public interface FullScreenListener {
        void onEnterFullScreen();
        void onExitFullScreen();
    }
}
