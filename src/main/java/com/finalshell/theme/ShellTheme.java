package com.finalshell.theme;

import java.awt.*;
import java.io.Serializable;

/**
 * 终端主题配置
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - ShellTheme
 */
public class ShellTheme implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private Color background;
    private Color foreground;
    private Color cursorColor;
    private Color selectionBackground;
    private Color selectionForeground;
    private Color[] colors = new Color[16];
    private boolean bold;
    private boolean useBrightColors;
    
    public ShellTheme() {
        this("Untitled");
    }
    
    public ShellTheme(String name) {
        this.name = name;
        initDefaultColors();
    }
    
    private void initDefaultColors() {
        background = Color.BLACK;
        foreground = Color.WHITE;
        cursorColor = Color.WHITE;
        selectionBackground = new Color(82, 82, 82);
        selectionForeground = Color.WHITE;
        
        // 标准8色
        colors[0] = new Color(0, 0, 0);       // Black
        colors[1] = new Color(205, 0, 0);     // Red
        colors[2] = new Color(0, 205, 0);     // Green
        colors[3] = new Color(205, 205, 0);   // Yellow
        colors[4] = new Color(0, 0, 238);     // Blue
        colors[5] = new Color(205, 0, 205);   // Magenta
        colors[6] = new Color(0, 205, 205);   // Cyan
        colors[7] = new Color(229, 229, 229); // White
        
        // 高亮8色
        colors[8] = new Color(127, 127, 127);  // Bright Black
        colors[9] = new Color(255, 0, 0);      // Bright Red
        colors[10] = new Color(0, 255, 0);     // Bright Green
        colors[11] = new Color(255, 255, 0);   // Bright Yellow
        colors[12] = new Color(92, 92, 255);   // Bright Blue
        colors[13] = new Color(255, 0, 255);   // Bright Magenta
        colors[14] = new Color(0, 255, 255);   // Bright Cyan
        colors[15] = new Color(255, 255, 255); // Bright White
    }
    
    public Color getColor(int index) {
        if (index >= 0 && index < colors.length) {
            return colors[index];
        }
        return foreground;
    }
    
    public void setColor(int index, Color color) {
        if (index >= 0 && index < colors.length) {
            colors[index] = color;
        }
    }
    
    public ShellTheme copy() {
        ShellTheme copy = new ShellTheme(name + " (Copy)");
        copy.background = background;
        copy.foreground = foreground;
        copy.cursorColor = cursorColor;
        copy.selectionBackground = selectionBackground;
        copy.selectionForeground = selectionForeground;
        copy.bold = bold;
        copy.useBrightColors = useBrightColors;
        System.arraycopy(colors, 0, copy.colors, 0, colors.length);
        return copy;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Color getBackground() { return background; }
    public void setBackground(Color background) { this.background = background; }
    
    public Color getForeground() { return foreground; }
    public void setForeground(Color foreground) { this.foreground = foreground; }
    
    public Color getCursorColor() { return cursorColor; }
    public void setCursorColor(Color cursorColor) { this.cursorColor = cursorColor; }
    
    public Color getSelectionBackground() { return selectionBackground; }
    public void setSelectionBackground(Color selectionBackground) { this.selectionBackground = selectionBackground; }
    
    public Color getSelectionForeground() { return selectionForeground; }
    public void setSelectionForeground(Color selectionForeground) { this.selectionForeground = selectionForeground; }
    
    public Color[] getColors() { return colors; }
    public void setColors(Color[] colors) { this.colors = colors; }
    
    public boolean isBold() { return bold; }
    public void setBold(boolean bold) { this.bold = bold; }
    
    public boolean isUseBrightColors() { return useBrightColors; }
    public void setUseBrightColors(boolean useBrightColors) { this.useBrightColors = useBrightColors; }
    
    @Override
    public String toString() {
        return name;
    }
}
