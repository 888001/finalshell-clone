package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 文件表格浮动面板
 */
public class FileTableFloatPanel extends JPanel {
    
    public FileTableFloatPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.dispose();
        super.paintComponent(g);
    }
}
