package com.finalshell.ui.filetree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 带提示文本的文本框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FileTree_UI_DeepAnalysis.md - JTextFieldHint
 */
public class JTextFieldHint extends JTextField {
    
    private String hint;
    private Color hintColor = Color.GRAY;
    private boolean showingHint = true;
    
    public JTextFieldHint() {
        super();
        init();
    }
    
    public JTextFieldHint(String hint) {
        super();
        this.hint = hint;
        init();
    }
    
    public JTextFieldHint(int columns) {
        super(columns);
        init();
    }
    
    public JTextFieldHint(String hint, int columns) {
        super(columns);
        this.hint = hint;
        init();
    }
    
    private void init() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingHint) {
                    showingHint = false;
                    repaint();
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    showingHint = true;
                    repaint();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (showingHint && getText().isEmpty() && hint != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(hintColor);
            g2.setFont(getFont());
            
            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int x = insets.left;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            
            g2.drawString(hint, x, y);
            g2.dispose();
        }
    }
    
    public String getHint() {
        return hint;
    }
    
    public void setHint(String hint) {
        this.hint = hint;
        repaint();
    }
    
    public Color getHintColor() {
        return hintColor;
    }
    
    public void setHintColor(Color color) {
        this.hintColor = color;
        repaint();
    }
}
