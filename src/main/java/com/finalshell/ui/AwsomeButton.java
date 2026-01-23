package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 图标按钮（使用FontAwesome字体）
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AwsomeButton extends JButton {
    
    private static Font awsomeFont;
    private String iconCode;
    private int iconSize;
    
    static {
        try {
            awsomeFont = new Font("FontAwesome", Font.PLAIN, 14);
        } catch (Exception e) {
            awsomeFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        }
    }
    
    public AwsomeButton() {
        this("");
    }
    
    public AwsomeButton(String iconCode) {
        this(iconCode, 14);
    }
    
    public AwsomeButton(String iconCode, int iconSize) {
        this.iconCode = iconCode;
        this.iconSize = iconSize;
        initUI();
    }
    
    private void initUI() {
        setFont(awsomeFont.deriveFont((float) iconSize));
        setText(iconCode);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(2, 4, 2, 4));
    }
    
    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
        setText(iconCode);
    }
    
    public String getIconCode() {
        return iconCode;
    }
    
    public void setIconSize(int size) {
        this.iconSize = size;
        setFont(awsomeFont.deriveFont((float) size));
    }
    
    public int getIconSize() {
        return iconSize;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g2);
        g2.dispose();
    }
}
