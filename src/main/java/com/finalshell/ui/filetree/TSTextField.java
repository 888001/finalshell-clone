package com.finalshell.ui.filetree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 自定义文本框（带搜索功能）
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FileTree_UI_DeepAnalysis.md - TSTextField
 */
public class TSTextField extends JTextField {
    
    private JButton clearButton;
    private boolean showClearButton = true;
    
    public TSTextField() {
        super();
        init();
    }
    
    public TSTextField(int columns) {
        super(columns);
        init();
    }
    
    public TSTextField(String text) {
        super(text);
        init();
    }
    
    private void init() {
        setLayout(new BorderLayout());
        
        clearButton = new JButton("×");
        clearButton.setPreferredSize(new Dimension(20, 20));
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setFocusPainted(false);
        clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearButton.setVisible(false);
        
        clearButton.addActionListener(e -> {
            setText("");
            requestFocus();
        });
        
        add(clearButton, BorderLayout.EAST);
        
        getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateClearButton();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateClearButton();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateClearButton();
            }
        });
    }
    
    private void updateClearButton() {
        if (showClearButton) {
            clearButton.setVisible(!getText().isEmpty());
        }
    }
    
    public void setShowClearButton(boolean show) {
        this.showClearButton = show;
        updateClearButton();
    }
    
    public boolean isShowClearButton() {
        return showClearButton;
    }
    
    @Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        if (showClearButton && clearButton != null && clearButton.isVisible()) {
            insets.right += clearButton.getPreferredSize().width;
        }
        return insets;
    }
}
