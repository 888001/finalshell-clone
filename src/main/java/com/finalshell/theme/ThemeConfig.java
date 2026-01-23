package com.finalshell.theme;

import java.awt.Color;

/**
 * 主题配置
 */
public class ThemeConfig {
    private String id;
    private String name;
    private boolean dark;
    
    // 主要颜色
    private Color primaryColor;
    private Color secondaryColor;
    private Color accentColor;
    
    // 背景颜色
    private Color backgroundColor;
    private Color panelColor;
    private Color inputColor;
    
    // 文本颜色
    private Color textColor;
    private Color textSecondaryColor;
    private Color textDisabledColor;
    
    // 边框颜色
    private Color borderColor;
    private Color dividerColor;
    
    // 状态颜色
    private Color successColor;
    private Color warningColor;
    private Color errorColor;
    private Color infoColor;
    
    // 选中/悬停颜色
    private Color selectionColor;
    private Color hoverColor;
    
    public ThemeConfig() {}
    
    public ThemeConfig(String id, String name, boolean dark) {
        this.id = id;
        this.name = name;
        this.dark = dark;
    }
    
    // 预设主题
    public static ThemeConfig lightTheme() {
        ThemeConfig theme = new ThemeConfig("light", "浅色主题", false);
        theme.primaryColor = new Color(70, 130, 180);
        theme.secondaryColor = new Color(100, 149, 237);
        theme.accentColor = new Color(30, 144, 255);
        theme.backgroundColor = Color.WHITE;
        theme.panelColor = new Color(245, 245, 245);
        theme.inputColor = Color.WHITE;
        theme.textColor = new Color(33, 33, 33);
        theme.textSecondaryColor = new Color(117, 117, 117);
        theme.textDisabledColor = new Color(189, 189, 189);
        theme.borderColor = new Color(224, 224, 224);
        theme.dividerColor = new Color(238, 238, 238);
        theme.successColor = new Color(76, 175, 80);
        theme.warningColor = new Color(255, 152, 0);
        theme.errorColor = new Color(244, 67, 54);
        theme.infoColor = new Color(33, 150, 243);
        theme.selectionColor = new Color(187, 222, 251);
        theme.hoverColor = new Color(245, 245, 245);
        return theme;
    }
    
    public static ThemeConfig darkTheme() {
        ThemeConfig theme = new ThemeConfig("dark", "深色主题", true);
        theme.primaryColor = new Color(100, 181, 246);
        theme.secondaryColor = new Color(144, 202, 249);
        theme.accentColor = new Color(64, 196, 255);
        theme.backgroundColor = new Color(30, 30, 30);
        theme.panelColor = new Color(45, 45, 45);
        theme.inputColor = new Color(55, 55, 55);
        theme.textColor = new Color(238, 238, 238);
        theme.textSecondaryColor = new Color(176, 176, 176);
        theme.textDisabledColor = new Color(97, 97, 97);
        theme.borderColor = new Color(66, 66, 66);
        theme.dividerColor = new Color(48, 48, 48);
        theme.successColor = new Color(129, 199, 132);
        theme.warningColor = new Color(255, 183, 77);
        theme.errorColor = new Color(239, 83, 80);
        theme.infoColor = new Color(79, 195, 247);
        theme.selectionColor = new Color(66, 66, 66);
        theme.hoverColor = new Color(55, 55, 55);
        return theme;
    }
    
    public static ThemeConfig draculaTheme() {
        ThemeConfig theme = new ThemeConfig("dracula", "Dracula", true);
        theme.primaryColor = new Color(189, 147, 249);
        theme.secondaryColor = new Color(255, 121, 198);
        theme.accentColor = new Color(139, 233, 253);
        theme.backgroundColor = new Color(40, 42, 54);
        theme.panelColor = new Color(68, 71, 90);
        theme.inputColor = new Color(68, 71, 90);
        theme.textColor = new Color(248, 248, 242);
        theme.textSecondaryColor = new Color(98, 114, 164);
        theme.textDisabledColor = new Color(68, 71, 90);
        theme.borderColor = new Color(68, 71, 90);
        theme.dividerColor = new Color(68, 71, 90);
        theme.successColor = new Color(80, 250, 123);
        theme.warningColor = new Color(255, 184, 108);
        theme.errorColor = new Color(255, 85, 85);
        theme.infoColor = new Color(139, 233, 253);
        theme.selectionColor = new Color(68, 71, 90);
        theme.hoverColor = new Color(68, 71, 90);
        return theme;
    }
    
    public static ThemeConfig monokaiTheme() {
        ThemeConfig theme = new ThemeConfig("monokai", "Monokai", true);
        theme.primaryColor = new Color(166, 226, 46);
        theme.secondaryColor = new Color(253, 151, 31);
        theme.accentColor = new Color(102, 217, 239);
        theme.backgroundColor = new Color(39, 40, 34);
        theme.panelColor = new Color(54, 55, 49);
        theme.inputColor = new Color(54, 55, 49);
        theme.textColor = new Color(248, 248, 242);
        theme.textSecondaryColor = new Color(117, 113, 94);
        theme.textDisabledColor = new Color(117, 113, 94);
        theme.borderColor = new Color(54, 55, 49);
        theme.dividerColor = new Color(54, 55, 49);
        theme.successColor = new Color(166, 226, 46);
        theme.warningColor = new Color(253, 151, 31);
        theme.errorColor = new Color(249, 38, 114);
        theme.infoColor = new Color(102, 217, 239);
        theme.selectionColor = new Color(73, 72, 62);
        theme.hoverColor = new Color(64, 65, 59);
        return theme;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public boolean isDark() { return dark; }
    public void setDark(boolean dark) { this.dark = dark; }
    
    public Color getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(Color primaryColor) { this.primaryColor = primaryColor; }
    
    public Color getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(Color secondaryColor) { this.secondaryColor = secondaryColor; }
    
    public Color getAccentColor() { return accentColor; }
    public void setAccentColor(Color accentColor) { this.accentColor = accentColor; }
    
    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }
    
    public Color getPanelColor() { return panelColor; }
    public void setPanelColor(Color panelColor) { this.panelColor = panelColor; }
    
    public Color getInputColor() { return inputColor; }
    public void setInputColor(Color inputColor) { this.inputColor = inputColor; }
    
    public Color getTextColor() { return textColor; }
    public void setTextColor(Color textColor) { this.textColor = textColor; }
    
    public Color getTextSecondaryColor() { return textSecondaryColor; }
    public void setTextSecondaryColor(Color textSecondaryColor) { this.textSecondaryColor = textSecondaryColor; }
    
    public Color getTextDisabledColor() { return textDisabledColor; }
    public void setTextDisabledColor(Color textDisabledColor) { this.textDisabledColor = textDisabledColor; }
    
    public Color getBorderColor() { return borderColor; }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }
    
    public Color getDividerColor() { return dividerColor; }
    public void setDividerColor(Color dividerColor) { this.dividerColor = dividerColor; }
    
    public Color getSuccessColor() { return successColor; }
    public void setSuccessColor(Color successColor) { this.successColor = successColor; }
    
    public Color getWarningColor() { return warningColor; }
    public void setWarningColor(Color warningColor) { this.warningColor = warningColor; }
    
    public Color getErrorColor() { return errorColor; }
    public void setErrorColor(Color errorColor) { this.errorColor = errorColor; }
    
    public Color getInfoColor() { return infoColor; }
    public void setInfoColor(Color infoColor) { this.infoColor = infoColor; }
    
    public Color getSelectionColor() { return selectionColor; }
    public void setSelectionColor(Color selectionColor) { this.selectionColor = selectionColor; }
    
    public Color getHoverColor() { return hoverColor; }
    public void setHoverColor(Color hoverColor) { this.hoverColor = hoverColor; }
}
