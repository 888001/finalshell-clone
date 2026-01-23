package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * 缓冲绘图包装器 - HiDPI支持
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md - BufferedWrap
 */
public class BufferedWrap {
    
    private BufferedPaint paintCallback;
    private BufferedImage buffer;
    private Component component;
    private Dimension dimension = new Dimension();
    private float scale = 2.0f;
    private boolean useTransform = false;
    private boolean useAlpha = true;
    private String displayId = "";
    
    public BufferedWrap(Component component, BufferedPaint callback) {
        this.component = component;
        this.paintCallback = callback;
    }
    
    /**
     * 绑定绘制
     */
    public void bindPaint(Graphics g) {
        if (component == null) return;
        bindPaint(g, getHiDPIScale(g), component.getWidth(), component.getHeight());
    }
    
    /**
     * 绑定绘制 (指定缩放)
     */
    public void bindPaint(Graphics g, float hidpiScale, int cwidth, int cheight) {
        if (cwidth <= 0 || cheight <= 0) return;
        
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();
        
        // 检测显示器切换
        String currentDisplayId = getDisplayId();
        if (!displayId.equals(currentDisplayId)) {
            displayId = currentDisplayId;
            buffer = null;
        }
        
        // 计算缓冲尺寸
        int bufferWidth = (int) (cwidth * hidpiScale);
        int bufferHeight = (int) (cheight * hidpiScale);
        
        // 创建或更新缓冲图像
        if (buffer == null || 
            buffer.getWidth() != bufferWidth || 
            buffer.getHeight() != bufferHeight) {
            buffer = createBuffer(bufferWidth, bufferHeight);
        }
        
        if (buffer != null) {
            Graphics2D bufferG = buffer.createGraphics();
            
            // 设置缩放
            if (hidpiScale != 1.0f) {
                bufferG.scale(hidpiScale, hidpiScale);
            }
            
            // 设置渲染提示
            bufferG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                     RenderingHints.VALUE_ANTIALIAS_ON);
            bufferG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                                     RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            
            // 清除背景
            if (useAlpha) {
                bufferG.setComposite(AlphaComposite.Clear);
                bufferG.fillRect(0, 0, cwidth, cheight);
                bufferG.setComposite(AlphaComposite.SrcOver);
            }
            
            // 执行绘图回调
            if (paintCallback != null) {
                paintCallback.paintBuffer(bufferG);
            }
            
            bufferG.dispose();
            
            // 绘制缓冲到屏幕
            g2d.setTransform(new AffineTransform());
            g2d.drawImage(buffer, 0, 0, cwidth, cheight, null);
            g2d.setTransform(originalTransform);
        }
    }
    
    /**
     * 创建缓冲图像
     */
    private BufferedImage createBuffer(int width, int height) {
        int imageType = useAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        return new BufferedImage(width, height, imageType);
    }
    
    /**
     * 获取HiDPI缩放比例
     */
    private float getHiDPIScale(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform transform = g2d.getTransform();
            return (float) transform.getScaleX();
        }
        return 1.0f;
    }
    
    /**
     * 获取显示器ID
     */
    private String getDisplayId() {
        if (component != null) {
            GraphicsConfiguration gc = component.getGraphicsConfiguration();
            if (gc != null) {
                return gc.getDevice().getIDstring();
            }
        }
        return "";
    }
    
    public boolean isAlpha() { return useAlpha; }
    public void setAlpha(boolean alpha) { this.useAlpha = alpha; }
    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = scale; }
    
    /**
     * 缓冲绘图回调接口
     */
    public interface BufferedPaint {
        void paintBuffer(Graphics2D g);
    }
}
