package com.finalshell.ui.font;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 字体集合管理
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Font_Menu_FullScreen_UI_DeepAnalysis.md - FontSet
 */
public class FontSet {
    
    private static FontSet instance;
    private List<String> availableFonts;
    private Map<String, Font> fontCache = new HashMap<>();
    
    private FontSet() {
        loadAvailableFonts();
    }
    
    public static synchronized FontSet getInstance() {
        if (instance == null) {
            instance = new FontSet();
        }
        return instance;
    }
    
    private void loadAvailableFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        availableFonts = new ArrayList<>(Arrays.asList(fontNames));
    }
    
    public List<String> getAvailableFonts() {
        return new ArrayList<>(availableFonts);
    }
    
    public List<String> getMonospaceFonts() {
        List<String> monoFonts = new ArrayList<>();
        String[] commonMono = {"Consolas", "Courier New", "Lucida Console", 
            "Monaco", "Menlo", "DejaVu Sans Mono", "Source Code Pro"};
        
        for (String font : commonMono) {
            if (availableFonts.contains(font)) {
                monoFonts.add(font);
            }
        }
        
        // 添加其他可能的等宽字体
        for (String font : availableFonts) {
            if (font.toLowerCase().contains("mono") && !monoFonts.contains(font)) {
                monoFonts.add(font);
            }
        }
        
        return monoFonts;
    }
    
    public Font getFont(String name, int style, int size) {
        String key = name + "_" + style + "_" + size;
        Font font = fontCache.get(key);
        
        if (font == null) {
            font = new Font(name, style, size);
            fontCache.put(key, font);
        }
        
        return font;
    }
    
    public boolean isFontAvailable(String fontName) {
        return availableFonts.contains(fontName);
    }
    
    public void clearCache() {
        fontCache.clear();
    }
}
