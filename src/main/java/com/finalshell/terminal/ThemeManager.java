package com.finalshell.terminal;

import com.jediterm.terminal.emulator.ColorPalette;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/**
 * Terminal Theme Manager
 * 
 * Based on analysis of FinalShell 3.8.3
 * Manages terminal color themes loaded from JSON files
 */
public class ThemeManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ThemeManager.class);
    private static ThemeManager instance;
    
    private final Map<String, TerminalTheme> themes = new LinkedHashMap<>();
    private String currentThemeName = "Default";
    private final List<ThemeChangeListener> listeners = new ArrayList<>();
    
    private ThemeManager() {
        loadBuiltinThemes();
        loadCustomThemes();
    }
    
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    /**
     * Load built-in themes
     */
    private void loadBuiltinThemes() {
        // Default theme
        themes.put("Default", TerminalTheme.createDefault());
        
        // Dark themes
        themes.put("Dark", new TerminalTheme(
            "Dark",
            new Color(30, 30, 30),      // background
            new Color(220, 220, 220),   // foreground
            new Color(0, 120, 215),     // cursor
            createDarkPalette()
        ));
        
        themes.put("Monokai", new TerminalTheme(
            "Monokai",
            new Color(39, 40, 34),
            new Color(248, 248, 242),
            new Color(249, 38, 114),
            createMonokaiPalette()
        ));
        
        themes.put("Solarized Dark", new TerminalTheme(
            "Solarized Dark",
            new Color(0, 43, 54),
            new Color(131, 148, 150),
            new Color(211, 54, 130),
            createSolarizedDarkPalette()
        ));
        
        themes.put("Dracula", new TerminalTheme(
            "Dracula",
            new Color(40, 42, 54),
            new Color(248, 248, 242),
            new Color(255, 121, 198),
            createDraculaPalette()
        ));
        
        // Light themes
        themes.put("Light", new TerminalTheme(
            "Light",
            new Color(255, 255, 255),
            new Color(30, 30, 30),
            new Color(0, 120, 215),
            createLightPalette()
        ));
        
        themes.put("Solarized Light", new TerminalTheme(
            "Solarized Light",
            new Color(253, 246, 227),
            new Color(101, 123, 131),
            new Color(211, 54, 130),
            createSolarizedLightPalette()
        ));
        
        logger.info("Loaded {} built-in themes", themes.size());
    }
    
    /**
     * Load custom themes from resources
     */
    private void loadCustomThemes() {
        try {
            // Try to load from resources/theme directory
            InputStream is = getClass().getResourceAsStream("/resources/theme/themes.properties");
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                // Load additional themes from properties
                logger.info("Loaded custom theme properties");
            }
        } catch (Exception e) {
            logger.debug("No custom themes found: {}", e.getMessage());
        }
    }
    
    /**
     * Get all theme names
     */
    public List<String> getThemeNames() {
        return new ArrayList<>(themes.keySet());
    }
    
    /**
     * Get theme by name
     */
    public TerminalTheme getTheme(String name) {
        return themes.getOrDefault(name, themes.get("Default"));
    }
    
    /**
     * Get current theme
     */
    public TerminalTheme getCurrentTheme() {
        return getTheme(currentThemeName);
    }
    
    /**
     * Set current theme
     */
    public void setCurrentTheme(String themeName) {
        if (themes.containsKey(themeName)) {
            this.currentThemeName = themeName;
            fireThemeChanged();
            logger.info("Theme changed to: {}", themeName);
        }
    }
    
    /**
     * Add theme change listener
     */
    public void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove theme change listener
     */
    public void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void fireThemeChanged() {
        TerminalTheme theme = getCurrentTheme();
        for (ThemeChangeListener listener : listeners) {
            try {
                listener.onThemeChanged(theme);
            } catch (Exception e) {
                logger.error("Theme change listener error", e);
            }
        }
    }
    
    // Color palette creators
    private Color[] createDarkPalette() {
        return new Color[] {
            new Color(0, 0, 0),         // Black
            new Color(205, 49, 49),     // Red
            new Color(13, 188, 121),    // Green
            new Color(229, 229, 16),    // Yellow
            new Color(36, 114, 200),    // Blue
            new Color(188, 63, 188),    // Magenta
            new Color(17, 168, 205),    // Cyan
            new Color(229, 229, 229),   // White
            new Color(102, 102, 102),   // Bright Black
            new Color(241, 76, 76),     // Bright Red
            new Color(35, 209, 139),    // Bright Green
            new Color(245, 245, 67),    // Bright Yellow
            new Color(59, 142, 234),    // Bright Blue
            new Color(214, 112, 214),   // Bright Magenta
            new Color(41, 184, 219),    // Bright Cyan
            new Color(255, 255, 255)    // Bright White
        };
    }
    
    private Color[] createMonokaiPalette() {
        return new Color[] {
            new Color(39, 40, 34),
            new Color(249, 38, 114),
            new Color(166, 226, 46),
            new Color(244, 191, 117),
            new Color(102, 217, 239),
            new Color(174, 129, 255),
            new Color(161, 239, 228),
            new Color(248, 248, 242),
            new Color(117, 113, 94),
            new Color(249, 38, 114),
            new Color(166, 226, 46),
            new Color(244, 191, 117),
            new Color(102, 217, 239),
            new Color(174, 129, 255),
            new Color(161, 239, 228),
            new Color(249, 248, 245)
        };
    }
    
    private Color[] createSolarizedDarkPalette() {
        return new Color[] {
            new Color(7, 54, 66),
            new Color(220, 50, 47),
            new Color(133, 153, 0),
            new Color(181, 137, 0),
            new Color(38, 139, 210),
            new Color(211, 54, 130),
            new Color(42, 161, 152),
            new Color(238, 232, 213),
            new Color(0, 43, 54),
            new Color(203, 75, 22),
            new Color(88, 110, 117),
            new Color(101, 123, 131),
            new Color(131, 148, 150),
            new Color(108, 113, 196),
            new Color(147, 161, 161),
            new Color(253, 246, 227)
        };
    }
    
    private Color[] createSolarizedLightPalette() {
        return new Color[] {
            new Color(238, 232, 213),
            new Color(220, 50, 47),
            new Color(133, 153, 0),
            new Color(181, 137, 0),
            new Color(38, 139, 210),
            new Color(211, 54, 130),
            new Color(42, 161, 152),
            new Color(7, 54, 66),
            new Color(253, 246, 227),
            new Color(203, 75, 22),
            new Color(147, 161, 161),
            new Color(131, 148, 150),
            new Color(101, 123, 131),
            new Color(108, 113, 196),
            new Color(88, 110, 117),
            new Color(0, 43, 54)
        };
    }
    
    private Color[] createDraculaPalette() {
        return new Color[] {
            new Color(33, 34, 44),
            new Color(255, 85, 85),
            new Color(80, 250, 123),
            new Color(241, 250, 140),
            new Color(189, 147, 249),
            new Color(255, 121, 198),
            new Color(139, 233, 253),
            new Color(248, 248, 242),
            new Color(98, 114, 164),
            new Color(255, 110, 103),
            new Color(105, 255, 148),
            new Color(255, 255, 165),
            new Color(214, 172, 255),
            new Color(255, 146, 223),
            new Color(164, 255, 255),
            new Color(255, 255, 255)
        };
    }
    
    private Color[] createLightPalette() {
        return new Color[] {
            new Color(0, 0, 0),
            new Color(205, 49, 49),
            new Color(0, 128, 0),
            new Color(128, 128, 0),
            new Color(0, 0, 205),
            new Color(128, 0, 128),
            new Color(0, 128, 128),
            new Color(192, 192, 192),
            new Color(128, 128, 128),
            new Color(255, 0, 0),
            new Color(0, 255, 0),
            new Color(255, 255, 0),
            new Color(0, 0, 255),
            new Color(255, 0, 255),
            new Color(0, 255, 255),
            new Color(255, 255, 255)
        };
    }
    
    /**
     * Theme change listener
     */
    public interface ThemeChangeListener {
        void onThemeChanged(TerminalTheme theme);
    }
}
