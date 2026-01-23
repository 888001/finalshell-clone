package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 自定义分层面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - MyLayeredPane
 */
public class MyLayeredPane extends JLayeredPane {
    
    public static final Integer DEFAULT_LAYER = 0;
    public static final Integer PALETTE_LAYER = 100;
    public static final Integer MODAL_LAYER = 200;
    public static final Integer POPUP_LAYER = 300;
    public static final Integer DRAG_LAYER = 400;
    public static final Integer FRAME_CONTENT_LAYER = -30000;
    
    private Component contentPane;
    
    public MyLayeredPane() {
        setLayout(null);
    }
    
    public void setContentPane(Component content) {
        if (contentPane != null) {
            remove(contentPane);
        }
        contentPane = content;
        add(content, FRAME_CONTENT_LAYER);
        revalidate();
    }
    
    public Component getContentPane() {
        return contentPane;
    }
    
    public void addToLayer(Component comp, Integer layer) {
        add(comp, layer);
    }
    
    public void addToDefaultLayer(Component comp) {
        add(comp, DEFAULT_LAYER);
    }
    
    public void addToPopupLayer(Component comp) {
        add(comp, POPUP_LAYER);
    }
    
    public void addToModalLayer(Component comp) {
        add(comp, MODAL_LAYER);
    }
    
    public void addToDragLayer(Component comp) {
        add(comp, DRAG_LAYER);
    }
    
    @Override
    public void doLayout() {
        if (contentPane != null) {
            contentPane.setBounds(0, 0, getWidth(), getHeight());
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (contentPane != null) {
            return contentPane.getPreferredSize();
        }
        return super.getPreferredSize();
    }
}
