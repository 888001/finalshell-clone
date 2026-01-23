package com.finalshell.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * 图像资源管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: BatchClasses_Analysis.md - ImageManager
 */
public class ImageManager {
    
    private static ImageManager instance;
    private Map<String, ImageIcon> iconCache = new HashMap<>();
    private Map<String, Image> imageCache = new HashMap<>();
    private String iconPath = "/icons/";
    
    private ImageManager() {
        loadDefaultIcons();
    }
    
    public static synchronized ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }
    
    private void loadDefaultIcons() {
        String[] defaultIcons = {
            "folder", "file", "ssh", "sftp", "terminal", 
            "connect", "disconnect", "settings", "add", "delete",
            "edit", "refresh", "search", "copy", "paste",
            "upload", "download", "home", "back", "forward"
        };
        
        for (String name : defaultIcons) {
            getIcon(name);
        }
    }
    
    public ImageIcon getIcon(String name) {
        if (iconCache.containsKey(name)) {
            return iconCache.get(name);
        }
        
        ImageIcon icon = loadIcon(name);
        if (icon != null) {
            iconCache.put(name, icon);
        }
        return icon;
    }
    
    public ImageIcon getIcon(String name, int width, int height) {
        String key = name + "_" + width + "x" + height;
        if (iconCache.containsKey(key)) {
            return iconCache.get(key);
        }
        
        ImageIcon original = getIcon(name);
        if (original == null) {
            return null;
        }
        
        Image scaled = original.getImage().getScaledInstance(width, height, 
            Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaled);
        iconCache.put(key, scaledIcon);
        return scaledIcon;
    }
    
    private ImageIcon loadIcon(String name) {
        String[] extensions = {".png", ".gif", ".jpg", ".ico"};
        
        for (String ext : extensions) {
            String path = iconPath + name + ext;
            URL url = getClass().getResource(path);
            if (url != null) {
                return new ImageIcon(url);
            }
        }
        
        File iconDir = new File("icons");
        if (iconDir.exists()) {
            for (String ext : extensions) {
                File file = new File(iconDir, name + ext);
                if (file.exists()) {
                    return new ImageIcon(file.getAbsolutePath());
                }
            }
        }
        
        return createPlaceholderIcon(name);
    }
    
    private ImageIcon createPlaceholderIcon(String name) {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(new Color(200, 200, 200));
        g.fillRect(0, 0, size, size);
        g.setColor(new Color(100, 100, 100));
        g.drawRect(0, 0, size - 1, size - 1);
        
        if (name != null && !name.isEmpty()) {
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            String initial = name.substring(0, 1).toUpperCase();
            FontMetrics fm = g.getFontMetrics();
            int x = (size - fm.stringWidth(initial)) / 2;
            int y = (size - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(initial, x, y);
        }
        
        g.dispose();
        return new ImageIcon(image);
    }
    
    public Image getImage(String name) {
        if (imageCache.containsKey(name)) {
            return imageCache.get(name);
        }
        
        ImageIcon icon = getIcon(name);
        if (icon != null) {
            Image image = icon.getImage();
            imageCache.put(name, image);
            return image;
        }
        return null;
    }
    
    public void clearCache() {
        iconCache.clear();
        imageCache.clear();
    }
    
    public void setIconPath(String path) {
        this.iconPath = path;
        clearCache();
    }
}
