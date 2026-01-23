package com.finalshell.theme;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 背景图片工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class BgImgTools {
    
    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            return null;
        }
    }
    
    public static BufferedImage scaleImage(BufferedImage img, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, width, height, null);
        g.dispose();
        return scaled;
    }
    
    public static BufferedImage tileImage(BufferedImage img, int width, int height) {
        BufferedImage tiled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tiled.createGraphics();
        
        int imgW = img.getWidth();
        int imgH = img.getHeight();
        
        for (int x = 0; x < width; x += imgW) {
            for (int y = 0; y < height; y += imgH) {
                g.drawImage(img, x, y, null);
            }
        }
        
        g.dispose();
        return tiled;
    }
    
    public static BufferedImage centerImage(BufferedImage img, int width, int height, Color bgColor) {
        BufferedImage centered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = centered.createGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);
        
        int x = (width - img.getWidth()) / 2;
        int y = (height - img.getHeight()) / 2;
        g.drawImage(img, x, y, null);
        
        g.dispose();
        return centered;
    }
    
    public static BufferedImage fillImage(BufferedImage img, int width, int height) {
        double imgRatio = (double) img.getWidth() / img.getHeight();
        double targetRatio = (double) width / height;
        
        int newWidth, newHeight;
        if (imgRatio > targetRatio) {
            newHeight = height;
            newWidth = (int) (height * imgRatio);
        } else {
            newWidth = width;
            newHeight = (int) (width / imgRatio);
        }
        
        BufferedImage scaled = scaleImage(img, newWidth, newHeight);
        
        int x = (newWidth - width) / 2;
        int y = (newHeight - height) / 2;
        
        return scaled.getSubimage(x, y, width, height);
    }
    
    public static BufferedImage applyOpacity(BufferedImage img, float opacity) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return result;
    }
}
