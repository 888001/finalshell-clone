package com.finalshell.ui.font;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * 字体配置
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Font_Menu_FullScreen_UI_DeepAnalysis.md - FontConfig
 */
public class FontConfig {
    
    private String fontName = "Consolas";
    private int fontSize = 14;
    private int fontStyle = Font.PLAIN;
    private List<String> fallbackFonts = new ArrayList<>();
    
    public FontConfig() {
        fallbackFonts.add("微软雅黑");
        fallbackFonts.add("SimHei");
    }
    
    public FontConfig(String fontName, int fontSize) {
        this();
        this.fontName = fontName;
        this.fontSize = fontSize;
    }
    
    public Font toFont() {
        return new Font(fontName, fontStyle, fontSize);
    }
    
    public String getFontName() { return fontName; }
    public void setFontName(String fontName) { this.fontName = fontName; }
    
    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    
    public int getFontStyle() { return fontStyle; }
    public void setFontStyle(int fontStyle) { this.fontStyle = fontStyle; }
    
    public List<String> getFallbackFonts() { return fallbackFonts; }
    public void setFallbackFonts(List<String> fallbackFonts) { this.fallbackFonts = fallbackFonts; }
    
    public void addFallbackFont(String font) {
        if (!fallbackFonts.contains(font)) {
            fallbackFonts.add(font);
        }
    }
    
    @Override
    public String toString() {
        return fontName + ", " + fontSize + "pt";
    }
}
