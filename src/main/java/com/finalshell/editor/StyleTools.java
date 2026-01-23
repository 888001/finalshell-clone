package com.finalshell.editor;

import java.awt.Color;
import java.awt.Font;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * 样式工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class StyleTools {
    
    public static final String STYLE_DEFAULT = "default";
    public static final String STYLE_KEYWORD = "keyword";
    public static final String STYLE_STRING = "string";
    public static final String STYLE_COMMENT = "comment";
    public static final String STYLE_NUMBER = "number";
    public static final String STYLE_OPERATOR = "operator";
    
    public static void applyStyle(StyledDocument doc, String styleName, 
            Color foreground, boolean bold, boolean italic) {
        Style style = doc.addStyle(styleName, null);
        StyleConstants.setForeground(style, foreground);
        StyleConstants.setBold(style, bold);
        StyleConstants.setItalic(style, italic);
    }
    
    public static void applyDefaultStyles(StyledDocument doc) {
        applyStyle(doc, STYLE_DEFAULT, Color.BLACK, false, false);
        applyStyle(doc, STYLE_KEYWORD, new Color(0, 0, 128), true, false);
        applyStyle(doc, STYLE_STRING, new Color(0, 128, 0), false, false);
        applyStyle(doc, STYLE_COMMENT, Color.GRAY, false, true);
        applyStyle(doc, STYLE_NUMBER, new Color(128, 0, 0), false, false);
        applyStyle(doc, STYLE_OPERATOR, Color.BLACK, true, false);
    }
    
    public static void setFontSize(StyledDocument doc, String styleName, int size) {
        Style style = doc.getStyle(styleName);
        if (style != null) {
            StyleConstants.setFontSize(style, size);
        }
    }
    
    public static void setFontFamily(StyledDocument doc, String styleName, String family) {
        Style style = doc.getStyle(styleName);
        if (style != null) {
            StyleConstants.setFontFamily(style, family);
        }
    }
    
    public static void setBackground(StyledDocument doc, String styleName, Color color) {
        Style style = doc.getStyle(styleName);
        if (style != null) {
            StyleConstants.setBackground(style, color);
        }
    }
    
    public static Color parseColor(String hex) {
        if (hex == null || hex.isEmpty()) {
            return Color.BLACK;
        }
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        try {
            return new Color(Integer.parseInt(hex, 16));
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }
    }
    
    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", 
            color.getRed(), color.getGreen(), color.getBlue());
    }
}
