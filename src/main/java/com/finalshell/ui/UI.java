package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

/**
 * UI初始化和全局样式管理
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: UI_Analysis.md
 */
public class UI {
    
    public static int ENCRYPT_SEED = 324534;
    
    private MainWindowManager mainWindowManager;
    private Window activeWindow;
    
    // 字体定义
    public static Font FONT_10;
    public static Font FONT_11;
    public static Font FONT_12;
    public static Font FONT_13;
    public static Font FONT_14;
    public static Font FONT_15;
    public static Font FONT_16;
    public static Font FONT_17;
    public static Font FONT_18;
    public static Font FONT_19;
    public static Font FONT_20;
    public static Font FONT_30;
    
    // 颜色定义
    public static Color COLOR_BLACK = new Color(0, 0, 0);
    public static Color COLOR_DARK = new Color(20, 20, 20);
    public static Color COLOR_LIGHT_BG = new Color(223, 232, 243);
    public static Color COLOR_SELECTED = new Color(154, 195, 220, 80);
    public static Color COLOR_GRAY = new Color(0x555555);
    
    // 默认字体名称
    public static String DEFAULT_FONT_NAME = "SimSun";
    
    private static Font fontAwesomeRegular;
    private static Font fontAwesomeSolid;
    
    public UI() {
        long eventMask = AWTEvent.MOUSE_EVENT_MASK;
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event.getSource() instanceof Component && event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                if (mouseEvent.getID() == MouseEvent.MOUSE_ENTERED) {
                    Component component = (Component) event.getSource();
                    activeWindow = SwingUtilities.windowForComponent(component);
                }
            }
        }, eventMask);
    }
    
    public void showMainWindow(String name) {
        mainWindowManager.showWindow(name);
    }
    
    public void showMainWindow() {
        mainWindowManager.showDefaultWindow();
    }
    
    void initMainWindowManager() {
        mainWindowManager = new MainWindowManager();
        mainWindowManager.initialize();
    }
    
    public static void initFonts() {
        try {
            InputStream is = UI.class.getResourceAsStream("/fonts/fontawesome-webfont.ttf");
            if (is != null) {
                Font afont = Font.createFont(Font.TRUETYPE_FONT, is);
                FONT_10 = afont.deriveFont(Font.PLAIN, 10.0f);
                FONT_11 = afont.deriveFont(Font.PLAIN, 11.0f);
                FONT_12 = afont.deriveFont(Font.PLAIN, 12.0f);
                FONT_13 = afont.deriveFont(Font.PLAIN, 13.0f);
                FONT_14 = afont.deriveFont(Font.PLAIN, 14.0f);
                FONT_15 = afont.deriveFont(Font.PLAIN, 15.0f);
                FONT_16 = afont.deriveFont(Font.PLAIN, 16.0f);
                FONT_17 = afont.deriveFont(Font.PLAIN, 17.0f);
                FONT_18 = afont.deriveFont(Font.PLAIN, 18.0f);
                FONT_19 = afont.deriveFont(Font.PLAIN, 19.0f);
                FONT_20 = afont.deriveFont(Font.PLAIN, 20.0f);
                FONT_30 = afont.deriveFont(Font.PLAIN, 30.0f);
                is.close();
            } else {
                initDefaultFonts();
            }
            
            is = UI.class.getResourceAsStream("/fonts/fa-regular-400.ttf");
            if (is != null) {
                fontAwesomeRegular = Font.createFont(Font.TRUETYPE_FONT, is);
                is.close();
            }
            
            is = UI.class.getResourceAsStream("/fonts/fa-solid-900.ttf");
            if (is != null) {
                fontAwesomeSolid = Font.createFont(Font.TRUETYPE_FONT, is);
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            initDefaultFonts();
        }
        
        ToolTipManager.sharedInstance().setInitialDelay(300);
        ToolTipManager.sharedInstance().setDismissDelay(10000);
    }
    
    private static void initDefaultFonts() {
        Font baseFont = new Font(DEFAULT_FONT_NAME, Font.PLAIN, 12);
        FONT_10 = baseFont.deriveFont(10.0f);
        FONT_11 = baseFont.deriveFont(11.0f);
        FONT_12 = baseFont.deriveFont(12.0f);
        FONT_13 = baseFont.deriveFont(13.0f);
        FONT_14 = baseFont.deriveFont(14.0f);
        FONT_15 = baseFont.deriveFont(15.0f);
        FONT_16 = baseFont.deriveFont(16.0f);
        FONT_17 = baseFont.deriveFont(17.0f);
        FONT_18 = baseFont.deriveFont(18.0f);
        FONT_19 = baseFont.deriveFont(19.0f);
        FONT_20 = baseFont.deriveFont(10.0f);
        FONT_30 = baseFont.deriveFont(30.0f);
    }
    
    public static Font getFontAwesomeRegular(float size) {
        if (fontAwesomeRegular != null) {
            return fontAwesomeRegular.deriveFont(size);
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, (int) size);
    }
    
    public static Font getFontAwesomeSolid(float size) {
        if (fontAwesomeSolid != null) {
            return fontAwesomeSolid.deriveFont(size);
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, (int) size);
    }
    
    public Window getActiveWindow() {
        return activeWindow;
    }
    
    public MainWindowManager getMainWindowManager() {
        return mainWindowManager;
    }
}
