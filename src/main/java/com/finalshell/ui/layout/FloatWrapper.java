package com.finalshell.ui.layout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 浮动包装器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FloatWrapper extends JPanel {
    
    private Component content;
    private JPanel titleBar;
    private JLabel titleLabel;
    private JButton closeButton;
    private boolean floating;
    private Point dragOffset;
    
    public FloatWrapper(Component content, String title) {
        this.content = content;
        initUI(title);
    }
    
    private void initUI(String title) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(60, 60, 60));
        titleBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        
        closeButton = new JButton("×");
        closeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(60, 60, 60));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> setVisible(false));
        
        titleBar.add(titleLabel, BorderLayout.CENTER);
        titleBar.add(closeButton, BorderLayout.EAST);
        
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragOffset = e.getPoint();
            }
        });
        
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (floating && dragOffset != null) {
                    Point location = getLocation();
                    setLocation(location.x + e.getX() - dragOffset.x,
                               location.y + e.getY() - dragOffset.y);
                }
            }
        });
        
        add(titleBar, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
    
    public void setFloating(boolean floating) {
        this.floating = floating;
    }
    
    public boolean isFloating() {
        return floating;
    }
    
    public Component getContent() {
        return content;
    }
    
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    public String getTitle() {
        return titleLabel.getText();
    }
}
