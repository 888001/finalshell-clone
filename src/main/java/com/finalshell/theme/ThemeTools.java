package com.finalshell.theme;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * 主题工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - ThemeTools
 */
public class ThemeTools {
    
    private static final String THEME_DIR = "themes";
    
    public static List<ShellTheme> loadThemes() {
        List<ShellTheme> themes = new ArrayList<>();
        
        // 添加内置主题
        themes.add(createDefaultTheme());
        themes.add(createDarkTheme());
        themes.add(createLightTheme());
        themes.add(createMonokaiTheme());
        themes.add(createSolarizedDarkTheme());
        themes.add(createSolarizedLightTheme());
        
        // 加载用户自定义主题
        File themeDir = new File(THEME_DIR);
        if (themeDir.exists() && themeDir.isDirectory()) {
            File[] files = themeDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try {
                        ShellTheme theme = loadThemeFromFile(file);
                        if (theme != null) {
                            themes.add(theme);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return themes;
    }
    
    public static ShellTheme loadThemeFromFile(File file) {
        // TODO: 从JSON文件加载主题
        return null;
    }
    
    public static void saveThemeToFile(ShellTheme theme, File file) {
        // TODO: 保存主题到JSON文件
    }
    
    public static ShellTheme createDefaultTheme() {
        ShellTheme theme = new ShellTheme("Default");
        theme.setBackground(Color.BLACK);
        theme.setForeground(Color.WHITE);
        theme.setCursorColor(Color.WHITE);
        theme.setSelectionBackground(new Color(82, 82, 82));
        theme.setSelectionForeground(Color.WHITE);
        
        theme.setColor(0, new Color(0, 0, 0));       // Black
        theme.setColor(1, new Color(205, 0, 0));     // Red
        theme.setColor(2, new Color(0, 205, 0));     // Green
        theme.setColor(3, new Color(205, 205, 0));   // Yellow
        theme.setColor(4, new Color(0, 0, 238));     // Blue
        theme.setColor(5, new Color(205, 0, 205));   // Magenta
        theme.setColor(6, new Color(0, 205, 205));   // Cyan
        theme.setColor(7, new Color(229, 229, 229)); // White
        
        return theme;
    }
    
    public static ShellTheme createDarkTheme() {
        ShellTheme theme = new ShellTheme("Dark");
        theme.setBackground(new Color(30, 30, 30));
        theme.setForeground(new Color(204, 204, 204));
        theme.setCursorColor(new Color(204, 204, 204));
        theme.setSelectionBackground(new Color(38, 79, 120));
        theme.setSelectionForeground(Color.WHITE);
        
        theme.setColor(0, new Color(0, 0, 0));
        theme.setColor(1, new Color(197, 15, 31));
        theme.setColor(2, new Color(19, 161, 14));
        theme.setColor(3, new Color(193, 156, 0));
        theme.setColor(4, new Color(0, 55, 218));
        theme.setColor(5, new Color(136, 23, 152));
        theme.setColor(6, new Color(58, 150, 221));
        theme.setColor(7, new Color(204, 204, 204));
        
        return theme;
    }
    
    public static ShellTheme createLightTheme() {
        ShellTheme theme = new ShellTheme("Light");
        theme.setBackground(Color.WHITE);
        theme.setForeground(Color.BLACK);
        theme.setCursorColor(Color.BLACK);
        theme.setSelectionBackground(new Color(173, 214, 255));
        theme.setSelectionForeground(Color.BLACK);
        
        theme.setColor(0, new Color(0, 0, 0));
        theme.setColor(1, new Color(197, 15, 31));
        theme.setColor(2, new Color(19, 161, 14));
        theme.setColor(3, new Color(193, 156, 0));
        theme.setColor(4, new Color(0, 55, 218));
        theme.setColor(5, new Color(136, 23, 152));
        theme.setColor(6, new Color(58, 150, 221));
        theme.setColor(7, new Color(204, 204, 204));
        
        return theme;
    }
    
    public static ShellTheme createMonokaiTheme() {
        ShellTheme theme = new ShellTheme("Monokai");
        theme.setBackground(new Color(39, 40, 34));
        theme.setForeground(new Color(248, 248, 242));
        theme.setCursorColor(new Color(248, 248, 240));
        theme.setSelectionBackground(new Color(73, 72, 62));
        theme.setSelectionForeground(new Color(248, 248, 242));
        
        theme.setColor(0, new Color(39, 40, 34));
        theme.setColor(1, new Color(249, 38, 114));
        theme.setColor(2, new Color(166, 226, 46));
        theme.setColor(3, new Color(244, 191, 117));
        theme.setColor(4, new Color(102, 217, 239));
        theme.setColor(5, new Color(174, 129, 255));
        theme.setColor(6, new Color(161, 239, 228));
        theme.setColor(7, new Color(248, 248, 242));
        
        return theme;
    }
    
    public static ShellTheme createSolarizedDarkTheme() {
        ShellTheme theme = new ShellTheme("Solarized Dark");
        theme.setBackground(new Color(0, 43, 54));
        theme.setForeground(new Color(131, 148, 150));
        theme.setCursorColor(new Color(131, 148, 150));
        theme.setSelectionBackground(new Color(7, 54, 66));
        theme.setSelectionForeground(new Color(147, 161, 161));
        
        theme.setColor(0, new Color(7, 54, 66));
        theme.setColor(1, new Color(220, 50, 47));
        theme.setColor(2, new Color(133, 153, 0));
        theme.setColor(3, new Color(181, 137, 0));
        theme.setColor(4, new Color(38, 139, 210));
        theme.setColor(5, new Color(211, 54, 130));
        theme.setColor(6, new Color(42, 161, 152));
        theme.setColor(7, new Color(238, 232, 213));
        
        return theme;
    }
    
    public static ShellTheme createSolarizedLightTheme() {
        ShellTheme theme = new ShellTheme("Solarized Light");
        theme.setBackground(new Color(253, 246, 227));
        theme.setForeground(new Color(101, 123, 131));
        theme.setCursorColor(new Color(101, 123, 131));
        theme.setSelectionBackground(new Color(238, 232, 213));
        theme.setSelectionForeground(new Color(88, 110, 117));
        
        theme.setColor(0, new Color(7, 54, 66));
        theme.setColor(1, new Color(220, 50, 47));
        theme.setColor(2, new Color(133, 153, 0));
        theme.setColor(3, new Color(181, 137, 0));
        theme.setColor(4, new Color(38, 139, 210));
        theme.setColor(5, new Color(211, 54, 130));
        theme.setColor(6, new Color(42, 161, 152));
        theme.setColor(7, new Color(238, 232, 213));
        
        return theme;
    }
}
