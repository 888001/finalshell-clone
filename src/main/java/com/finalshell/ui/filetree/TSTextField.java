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
        // 设置边框样式，模拟WebTextField
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 30)
        ));
        
        // 设置背景和字体
        setBackground(Color.WHITE);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        // 创建清除按钮
        clearButton = new JButton("×");
        clearButton.setPreferredSize(new Dimension(16, 16));
        clearButton.setBorder(null);
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setFocusPainted(false);
        clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        clearButton.setForeground(new Color(140, 140, 140));
        clearButton.setVisible(false);
        
        clearButton.addActionListener(e -> {
            setText("");
            requestFocus();
        });
        
        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                clearButton.setForeground(new Color(80, 80, 80));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                clearButton.setForeground(new Color(140, 140, 140));
            }
        });
        
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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 绘制清除按钮
        if (showClearButton && !getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int buttonX = getWidth() - 25;
            int buttonY = (getHeight() - 16) / 2;
            
            clearButton.setBounds(buttonX, buttonY, 16, 16);
            clearButton.setVisible(true);
            
            g2.dispose();
        } else {
            clearButton.setVisible(false);
        }
    }
    
    @Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        if (showClearButton && !getText().isEmpty()) {
            insets.right += 25;
        }
        return insets;
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        updateClearButtonPosition();
    }
    
    private void updateClearButtonPosition() {
        if (clearButton != null && showClearButton && !getText().isEmpty()) {
            int buttonX = getWidth() - 25;
            int buttonY = (getHeight() - 16) / 2;
            clearButton.setBounds(buttonX, buttonY, 16, 16);
        }
    }
}
