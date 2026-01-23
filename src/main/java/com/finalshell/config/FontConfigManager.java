package com.finalshell.config;

/**
 * 字体配置管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FontConfigManager {
    
    private static FontConfigManager instance;
    private FontConfig fontConfig;
    
    private FontConfigManager() {
    }
    
    public static synchronized FontConfigManager getInstance() {
        if (instance == null) {
            instance = new FontConfigManager();
        }
        return instance;
    }
    
    public void setFont(String enFontName, String cnFontName, int fontSize) {
        FontSet enFontSet = new FontSet(enFontName, fontSize);
        FontSet cnFontSet = new FontSet(cnFontName, fontSize);
        FontSet symbolFontSet = new FontSet("Symbola", fontSize);
        
        FontConfig config = new FontConfig();
        config.setEnFont(enFontSet);
        config.setCnFont(cnFontSet);
        config.setSymbolFont(symbolFontSet);
        
        this.fontConfig = config;
    }
    
    public FontConfig getFontConfig() {
        return this.fontConfig;
    }
    
    public void setFontConfig(FontConfig fontConfig) {
        this.fontConfig = fontConfig;
    }
    
    public static class FontSet {
        private String fontName;
        private int fontSize;
        
        public FontSet(String fontName, int fontSize) {
            this.fontName = fontName;
            this.fontSize = fontSize;
        }
        
        public String getFontName() {
            return fontName;
        }
        
        public void setFontName(String fontName) {
            this.fontName = fontName;
        }
        
        public int getFontSize() {
            return fontSize;
        }
        
        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }
    }
    
    public static class FontConfig {
        private FontSet enFont;
        private FontSet cnFont;
        private FontSet symbolFont;
        
        public FontSet getEnFont() {
            return enFont;
        }
        
        public void setEnFont(FontSet enFont) {
            this.enFont = enFont;
        }
        
        public FontSet getCnFont() {
            return cnFont;
        }
        
        public void setCnFont(FontSet cnFont) {
            this.cnFont = cnFont;
        }
        
        public FontSet getSymbolFont() {
            return symbolFont;
        }
        
        public void setSymbolFont(FontSet symbolFont) {
            this.symbolFont = symbolFont;
        }
    }
}
