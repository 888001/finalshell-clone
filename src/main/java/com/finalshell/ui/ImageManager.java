package com.finalshell.ui;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片资源缓存管理器 - 对齐原版myssh实现
 * 
 * Based on analysis of myssh/ImageManager.java (29行)
 * 提供图片资源的缓存和加载功能
 */
public class ImageManager {
    
    // 图片缓存映射表 - 对齐原版myssh
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();
    
    private ImageManager() {
        // 私有构造函数，防止实例化
    }
    
    /**
     * 获取图片资源 - 对齐原版myssh的加载方式
     */
    public ImageIcon getImage(String path) {
        return getImage(this.getClass(), path);
    }
    
    /**
     * 获取图片资源（指定类路径） - 对齐原版myssh
     */
    public ImageIcon getImage(Class<?> nearClass, String path) {
        String key = nearClass.getCanonicalName() + ":" + path;
        
        if (!imageCache.containsKey(key)) {
            try {
                URL resourceUrl = nearClass.getResource("/" + path);
                if (resourceUrl != null) {
                    ImageIcon icon = new ImageIcon(resourceUrl);
                    imageCache.put(key, icon);
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        
        return imageCache.get(key);
    }
    
}
