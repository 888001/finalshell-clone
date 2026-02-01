package com.finalshell.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource Loader - Loads icons, images, and theme files
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Resource_Files_Reference.md
 */
public class ResourceLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);
    
    private static ResourceLoader instance;
    
    // Use concurrent collections for thread safety and LRU cache for memory efficiency
    private final Map<String, Icon> iconCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<String, Image> imageCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<String, TerminalTheme> themeCache = new java.util.concurrent.ConcurrentHashMap<>();
    
    // Cache size limits to prevent memory leaks
    private static final int MAX_ICON_CACHE_SIZE = 200;
    private static final int MAX_IMAGE_CACHE_SIZE = 100;
    private static final int MAX_THEME_CACHE_SIZE = 50;
    
    private Image appLogo;
    
    public static ResourceLoader getInstance() {
        if (instance == null) {
            instance = new ResourceLoader();
        }
        return instance;
    }
    
    private ResourceLoader() {
    }
    
    public void init() {
        loadAppLogo();
        preloadIcons();
        logger.info("ResourceLoader initialized");
    }
    
    private void loadAppLogo() {
        appLogo = loadImage("resources/images/logo.png");
        if (appLogo == null) {
            logger.warn("App logo not found, using default");
        }
    }
    
    private void preloadIcons() {
        // Preload commonly used icons
        String[] icons = {
            "images/window_new.png",
            "images/folder_new.png",
            "images/folder.png",
            "images/folder-open.png",
            "images/collapseall.png",
            "images/expandall.png",
            "images/terminal.png",
            "images/screen.png",
            "images/config.png",
            "images/remove.png"
        };
        
        for (String path : icons) {
            getIcon(path);
        }
    }
    
    /**
     * Get icon by path, with caching
     */
    public Icon getIcon(String path) {
        if (iconCache.containsKey(path)) {
            return iconCache.get(path);
        }
        
        try {
            URL url = getClass().getClassLoader().getResource(path);
            if (url != null) {
                Icon icon = new ImageIcon(url);
                iconCache.put(path, icon);
                return icon;
            }
        } catch (Exception e) {
            logger.error("Failed to load icon: {}", path, e);
        }
        
        return null;
    }
    
    /**
     * Get scaled icon
     */
    public Icon getIcon(String path, int width, int height) {
        Icon original = getIcon(path);
        if (original instanceof ImageIcon) {
            Image img = ((ImageIcon) original).getImage();
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return original;
    }
    
    /**
     * Get image by path, with caching
     */
    public Image loadImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        
        try {
            URL url = getClass().getClassLoader().getResource(path);
            if (url != null) {
                Image image = new ImageIcon(url).getImage();
                
                // Check cache size and clean if necessary
                if (imageCache.size() >= MAX_IMAGE_CACHE_SIZE) {
                    cleanImageCache();
                }
                
                imageCache.put(path, image);
                return image;
            }
        } catch (Exception e) {
            logger.error("Failed to load image: {}", path, e);
        }
        
        return null;
    }
    
    /**
     * Clean cache to prevent memory leaks
     */
    private void cleanImageCache() {
        if (imageCache.size() > MAX_IMAGE_CACHE_SIZE / 2) {
            // Remove 25% of oldest entries (simplified LRU)
            int toRemove = imageCache.size() / 4;
            String[] keys = imageCache.keySet().toArray(new String[0]);
            for (int i = 0; i < toRemove && i < keys.length; i++) {
                imageCache.remove(keys[i]);
            }
            System.gc(); // Suggest garbage collection
        }
    }
    
    /**
     * Clear all caches to free memory
     */
    public void clearCaches() {
        iconCache.clear();
        imageCache.clear();
        themeCache.clear();
        System.gc();
        logger.info("Resource caches cleared");
    }
    
    /**
     * Get terminal theme by name
     */
    public TerminalTheme getTheme(String themeName) {
        if (themeCache.containsKey(themeName)) {
            return themeCache.get(themeName);
        }
        
        String path = "resources/theme/" + themeName + ".json";
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is != null) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                TerminalTheme theme = JSON.parseObject(json, TerminalTheme.class);
                themeCache.put(themeName, theme);
                return theme;
            }
        } catch (Exception e) {
            logger.error("Failed to load theme: {}", themeName, e);
        }
        
        // Return default theme
        return getDefaultTheme();
    }
    
    /**
     * Get default terminal theme
     */
    public TerminalTheme getDefaultTheme() {
        TerminalTheme theme = themeCache.get("Default");
        if (theme == null) {
            theme = new TerminalTheme();
            theme.setName("Default");
            theme.setForeground("#D4D4D4");
            theme.setBackground("#1E1E1E");
            theme.setCursor("#FFFFFF");
            themeCache.put("Default", theme);
        }
        return theme;
    }
    
    /**
     * List available theme names
     */
    public String[] getAvailableThemes() {
        // In production, this would scan the resources/theme directory
        return new String[] {
            "Default", "Dracula", "Monokai Soda", "One Dark", 
            "Solarized Dark", "Solarized Light", "Nord", "Gruvbox Dark"
        };
    }
    
    public Image getAppLogo() {
        return appLogo;
    }
    
    /**
     * Clear all caches
     */
    public void clearCache() {
        iconCache.clear();
        imageCache.clear();
        themeCache.clear();
    }
    
    /**
     * Terminal Theme configuration
     */
    public static class TerminalTheme {
        private String name;
        private String foreground;
        private String background;
        private String cursor;
        private String selection;
        
        private String black;
        private String red;
        private String green;
        private String yellow;
        private String blue;
        private String magenta;
        private String cyan;
        private String white;
        
        private String brightBlack;
        private String brightRed;
        private String brightGreen;
        private String brightYellow;
        private String brightBlue;
        private String brightMagenta;
        private String brightCyan;
        private String brightWhite;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getForeground() { return foreground; }
        public void setForeground(String foreground) { this.foreground = foreground; }
        
        public String getBackground() { return background; }
        public void setBackground(String background) { this.background = background; }
        
        public String getCursor() { return cursor; }
        public void setCursor(String cursor) { this.cursor = cursor; }
        
        public String getSelection() { return selection; }
        public void setSelection(String selection) { this.selection = selection; }
        
        public String getBlack() { return black; }
        public void setBlack(String black) { this.black = black; }
        
        public String getRed() { return red; }
        public void setRed(String red) { this.red = red; }
        
        public String getGreen() { return green; }
        public void setGreen(String green) { this.green = green; }
        
        public String getYellow() { return yellow; }
        public void setYellow(String yellow) { this.yellow = yellow; }
        
        public String getBlue() { return blue; }
        public void setBlue(String blue) { this.blue = blue; }
        
        public String getMagenta() { return magenta; }
        public void setMagenta(String magenta) { this.magenta = magenta; }
        
        public String getCyan() { return cyan; }
        public void setCyan(String cyan) { this.cyan = cyan; }
        
        public String getWhite() { return white; }
        public void setWhite(String white) { this.white = white; }
        
        public String getBrightBlack() { return brightBlack; }
        public void setBrightBlack(String brightBlack) { this.brightBlack = brightBlack; }
        
        public String getBrightRed() { return brightRed; }
        public void setBrightRed(String brightRed) { this.brightRed = brightRed; }
        
        public String getBrightGreen() { return brightGreen; }
        public void setBrightGreen(String brightGreen) { this.brightGreen = brightGreen; }
        
        public String getBrightYellow() { return brightYellow; }
        public void setBrightYellow(String brightYellow) { this.brightYellow = brightYellow; }
        
        public String getBrightBlue() { return brightBlue; }
        public void setBrightBlue(String brightBlue) { this.brightBlue = brightBlue; }
        
        public String getBrightMagenta() { return brightMagenta; }
        public void setBrightMagenta(String brightMagenta) { this.brightMagenta = brightMagenta; }
        
        public String getBrightCyan() { return brightCyan; }
        public void setBrightCyan(String brightCyan) { this.brightCyan = brightCyan; }
        
        public String getBrightWhite() { return brightWhite; }
        public void setBrightWhite(String brightWhite) { this.brightWhite = brightWhite; }
        
        public Color getForegroundColor() {
            return parseColor(foreground, Color.LIGHT_GRAY);
        }
        
        public Color getBackgroundColor() {
            return parseColor(background, Color.DARK_GRAY);
        }
        
        public Color getCursorColor() {
            return parseColor(cursor, Color.WHITE);
        }
        
        private Color parseColor(String hex, Color defaultColor) {
            if (hex == null || hex.isEmpty()) {
                return defaultColor;
            }
            try {
                if (hex.startsWith("#")) {
                    hex = hex.substring(1);
                }
                return new Color(Integer.parseInt(hex, 16));
            } catch (Exception e) {
                return defaultColor;
            }
        }
    }
}
