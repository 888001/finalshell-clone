package com.finalshell.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * UI全局配置常量
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: UI_Package_Analysis.md - UIConfig
 */
public class UIConfig {
    
    // 颜色常量
    public static final Color BACKGROUND_WHITE = new Color(253, 253, 253);
    public static final Color BORDER_GRAY = new Color(150, 150, 150);
    public static final Color BORDER_LIGHT = new Color(200, 200, 200);
    public static final Color TRANSPARENT_GRAY = new Color(180, 180, 180, 80);
    
    public static final Color COLOR_NEAR_WHITE = Color.decode("#FCFCFC");
    public static final Color COLOR_LIGHT_GRAY = Color.decode("#F0F0F0");
    public static final Color COLOR_LINK_BLUE = Color.decode("#1C86EE");
    public static final Color COLOR_BORDER = Color.decode("#EBEBEB");
    public static final Color COLOR_LIGHT_BLUE = Color.decode("#ADD8E6");
    
    // 选中颜色
    public static final Color SELECTION_BG = new Color(51, 153, 255);
    public static final Color SELECTION_FG = Color.WHITE;
    public static final Color HOVER_BG = new Color(229, 243, 255);
    
    // 布局常量
    public static final int ROUND_RADIUS = 3;
    public static final int BORDER_WIDTH = 1;
    public static final int PADDING = 2;
    public static final int ICON_SIZE = 16;
    public static final int LARGE_ICON_SIZE = 24;
    
    // 字体
    private static Font defaultFont;
    private static Font monoFont;
    private static Font boldFont;
    
    static {
        defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        monoFont = new Font(Font.MONOSPACED, Font.PLAIN, 13);
        boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    }
    
    public static Font getDefaultFont() {
        return defaultFont;
    }
    
    public static Font getMonoFont() {
        return monoFont;
    }
    
    public static Font getBoldFont() {
        return boldFont;
    }
    
    public static void setDefaultFont(Font font) {
        defaultFont = font;
    }
    
    public static void setMonoFont(Font font) {
        monoFont = font;
    }
    
    // 图标路径
    public static final String ICON_FOLDER = "/icons/folder.png";
    public static final String ICON_FILE = "/icons/file.png";
    public static final String ICON_SSH = "/icons/ssh.png";
    public static final String ICON_RDP = "/icons/rdp.png";
    public static final String ICON_SFTP = "/icons/sftp.png";
    public static final String ICON_TERMINAL = "/icons/terminal.png";
    public static final String ICON_CONNECT = "/icons/connect.png";
    public static final String ICON_DISCONNECT = "/icons/disconnect.png";
    public static final String ICON_SETTINGS = "/icons/settings.png";
    public static final String ICON_ADD = "/icons/add.png";
    public static final String ICON_DELETE = "/icons/delete.png";
    public static final String ICON_EDIT = "/icons/edit.png";
    public static final String ICON_REFRESH = "/icons/refresh.png";
    
    private UIConfig() {}
}
