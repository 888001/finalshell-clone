package com.finalshell.terminal;

import java.awt.*;

/**
 * Terminal Theme - Color scheme for terminal
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TerminalTheme {
    
    private final String name;
    private final Color background;
    private final Color foreground;
    private final Color cursor;
    private final Color[] palette;
    
    public TerminalTheme(String name, Color background, Color foreground, Color cursor, Color[] palette) {
        this.name = name;
        this.background = background;
        this.foreground = foreground;
        this.cursor = cursor;
        this.palette = palette != null ? palette : createDefaultPalette();
    }
    
    /**
     * Create default theme
     */
    public static TerminalTheme createDefault() {
        return new TerminalTheme(
            "Default",
            new Color(0, 0, 0),
            new Color(187, 187, 187),
            new Color(0, 255, 0),
            createDefaultPalette()
        );
    }
    
    private static Color[] createDefaultPalette() {
        return new Color[] {
            new Color(0, 0, 0),         // 0 Black
            new Color(170, 0, 0),       // 1 Red
            new Color(0, 170, 0),       // 2 Green
            new Color(170, 85, 0),      // 3 Yellow
            new Color(0, 0, 170),       // 4 Blue
            new Color(170, 0, 170),     // 5 Magenta
            new Color(0, 170, 170),     // 6 Cyan
            new Color(170, 170, 170),   // 7 White
            new Color(85, 85, 85),      // 8 Bright Black
            new Color(255, 85, 85),     // 9 Bright Red
            new Color(85, 255, 85),     // 10 Bright Green
            new Color(255, 255, 85),    // 11 Bright Yellow
            new Color(85, 85, 255),     // 12 Bright Blue
            new Color(255, 85, 255),    // 13 Bright Magenta
            new Color(85, 255, 255),    // 14 Bright Cyan
            new Color(255, 255, 255)    // 15 Bright White
        };
    }
    
    // Getters
    public String getName() { return name; }
    public Color getBackground() { return background; }
    public Color getForeground() { return foreground; }
    public Color getCursor() { return cursor; }
    public Color[] getPalette() { return palette; }
    
    /**
     * Get color at palette index
     */
    public Color getColor(int index) {
        if (index >= 0 && index < palette.length) {
            return palette[index];
        }
        return foreground;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
