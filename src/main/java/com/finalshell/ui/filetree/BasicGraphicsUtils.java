package com.finalshell.ui.filetree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

/**
 * 基础图形绘制工具类
 * 提供常用的UI绘制方法
 */
public class BasicGraphicsUtils {
    
    private static final Insets DEFAULT_INSETS = new Insets(2, 2, 2, 2);
    private static final Insets GROOVE_INSETS = new Insets(2, 2, 2, 2);
    
    /**
     * 绘制凹陷边框
     */
    public static void drawLoweredBezel(Graphics g, int x, int y, int w, int h, 
            Color shadow, Color darkShadow, Color highlight, Color lightHighlight) {
        Color oldColor = g.getColor();
        g.translate(x, y);
        
        g.setColor(shadow);
        g.drawLine(0, 0, w - 1, 0);
        g.drawLine(0, 1, 0, h - 2);
        
        g.setColor(darkShadow);
        g.drawLine(1, 1, w - 3, 1);
        g.drawLine(1, 2, 1, h - 3);
        
        g.setColor(lightHighlight);
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);
        
        g.setColor(highlight);
        g.drawLine(w - 2, 1, w - 2, h - 3);
        g.drawLine(1, h - 2, w - 2, h - 2);
        
        g.translate(-x, -y);
        g.setColor(oldColor);
    }
    
    /**
     * 获取凹陷边框的Insets
     */
    public static Insets getLoweredInsets() {
        return GROOVE_INSETS;
    }
    
    /**
     * 绘制凹槽边框
     */
    public static void drawGroove(Graphics g, int x, int y, int w, int h, 
            Color shadow, Color highlight) {
        Color oldColor = g.getColor();
        g.translate(x, y);
        
        g.setColor(shadow);
        g.drawRect(0, 0, w - 2, h - 2);
        
        g.setColor(highlight);
        g.drawLine(1, h - 3, 1, 1);
        g.drawLine(1, 1, w - 3, 1);
        g.drawLine(0, h - 1, w - 1, h - 1);
        g.drawLine(w - 1, h - 1, w - 1, 0);
        
        g.translate(-x, -y);
        g.setColor(oldColor);
    }
    
    /**
     * 获取凹槽边框的Insets
     */
    public static Insets getGrooveInsets() {
        return DEFAULT_INSETS;
    }
    
    /**
     * 绘制按钮边框
     */
    public static void drawBezel(Graphics g, int x, int y, int w, int h, 
            boolean isPressed, boolean isDefault,
            Color shadow, Color darkShadow, Color highlight, Color lightHighlight) {
        Color oldColor = g.getColor();
        g.translate(x, y);
        
        if (isPressed && isDefault) {
            g.setColor(darkShadow);
            g.drawRect(0, 0, w - 1, h - 1);
            g.setColor(shadow);
            g.drawRect(1, 1, w - 3, h - 3);
        } else if (isPressed) {
            drawLoweredBezel(g, x, y, w, h, shadow, darkShadow, highlight, lightHighlight);
        } else if (isDefault) {
            g.setColor(darkShadow);
            g.drawRect(0, 0, w - 1, h - 1);
            g.setColor(lightHighlight);
            g.drawLine(1, 1, 1, h - 3);
            g.drawLine(2, 1, w - 3, 1);
            g.setColor(highlight);
            g.drawLine(2, 2, 2, h - 4);
            g.drawLine(3, 2, w - 4, 2);
            g.setColor(shadow);
            g.drawLine(2, h - 3, w - 3, h - 3);
            g.drawLine(w - 3, 2, w - 3, h - 4);
            g.setColor(darkShadow);
            g.drawLine(1, h - 2, w - 2, h - 2);
            g.drawLine(w - 2, h - 2, w - 2, 1);
        } else {
            g.setColor(lightHighlight);
            g.drawLine(0, 0, 0, h - 1);
            g.drawLine(1, 0, w - 2, 0);
            g.setColor(highlight);
            g.drawLine(1, 1, 1, h - 3);
            g.drawLine(2, 1, w - 3, 1);
            g.setColor(shadow);
            g.drawLine(1, h - 2, w - 2, h - 2);
            g.drawLine(w - 2, 1, w - 2, h - 3);
            g.setColor(darkShadow);
            g.drawLine(0, h - 1, w - 1, h - 1);
            g.drawLine(w - 1, h - 1, w - 1, 0);
        }
        
        g.translate(-x, -y);
        g.setColor(oldColor);
    }
    
    /**
     * 绘制虚线矩形
     */
    public static void drawDashedRect(Graphics g, int x, int y, int width, int height) {
        int vx, vy;
        for (vx = x; vx < (x + width); vx += 2) {
            g.fillRect(vx, y, 1, 1);
            g.fillRect(vx, y + height - 1, 1, 1);
        }
        for (vy = y; vy < (y + height); vy += 2) {
            g.fillRect(x, vy, 1, 1);
            g.fillRect(x + width - 1, vy, 1, 1);
        }
    }
    
    /**
     * 绘制字符串并返回宽度
     */
    public static int drawString(Graphics g, String text, int x, int y) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        
        g.drawString(text, x, y);
        return g.getFontMetrics().stringWidth(text);
    }
    
    /**
     * 绘制带下划线的字符串
     */
    public static int drawStringUnderlineCharAt(Graphics g, String text, 
            int underlinedIndex, int x, int y) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        int width = drawString(g, text, x, y);
        
        if (underlinedIndex >= 0 && underlinedIndex < text.length()) {
            FontMetrics fm = g.getFontMetrics();
            int underlineX = x + fm.stringWidth(text.substring(0, underlinedIndex));
            int underlineWidth = fm.charWidth(text.charAt(underlinedIndex));
            int underlineY = y + 1;
            g.fillRect(underlineX, underlineY, underlineWidth, 1);
        }
        
        return width;
    }
    
    /**
     * 获取首选按钮尺寸
     */
    public static Dimension getPreferredButtonSize(AbstractButton b, int textIconGap) {
        if (b.getComponentCount() > 0) {
            return null;
        }
        
        Icon icon = b.getIcon();
        String text = b.getText();
        
        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);
        
        Rectangle iconR = new Rectangle();
        Rectangle textR = new Rectangle();
        Rectangle viewR = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
        
        SwingUtilities.layoutCompoundLabel(
            b, fm, text, icon,
            b.getVerticalAlignment(), b.getHorizontalAlignment(),
            b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
            viewR, iconR, textR, (text == null ? 0 : textIconGap)
        );
        
        Rectangle r = iconR.union(textR);
        
        Insets insets = b.getInsets();
        r.width += insets.left + insets.right;
        r.height += insets.top + insets.bottom;
        
        return r.getSize();
    }
}
