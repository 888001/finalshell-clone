package com.finalshell.layout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/**
 * 布局管理器
 */
public class LayoutManager {
    private static final Logger logger = LoggerFactory.getLogger(LayoutManager.class);
    
    private static final String LAYOUT_DIR = "layouts";
    private static final String CURRENT_LAYOUT = "current.json";
    private static final String DEFAULT_LAYOUT = "default";
    
    private static LayoutManager instance;
    
    private final Path layoutPath;
    private final Map<String, LayoutConfig> layouts = new HashMap<>();
    private LayoutConfig currentLayout;
    
    private LayoutManager() {
        String userHome = System.getProperty("user.home");
        this.layoutPath = Paths.get(userHome, ".finalshell", LAYOUT_DIR);
        
        try {
            Files.createDirectories(layoutPath);
        } catch (IOException e) {
            logger.error("创建布局目录失败", e);
        }
        
        loadLayouts();
    }
    
    public static synchronized LayoutManager getInstance() {
        if (instance == null) {
            instance = new LayoutManager();
        }
        return instance;
    }
    
    /**
     * 加载所有布局
     */
    private void loadLayouts() {
        try {
            Files.list(layoutPath)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(this::loadLayout);
            
            // 加载当前布局
            Path currentPath = layoutPath.resolve(CURRENT_LAYOUT);
            if (Files.exists(currentPath)) {
                currentLayout = loadLayoutFromFile(currentPath);
            }
            
        } catch (IOException e) {
            logger.error("加载布局失败", e);
        }
    }
    
    private void loadLayout(Path path) {
        try {
            LayoutConfig config = loadLayoutFromFile(path);
            if (config != null && config.getName() != null) {
                layouts.put(config.getName(), config);
            }
        } catch (Exception e) {
            logger.error("加载布局文件失败: {}", path, e);
        }
    }
    
    private LayoutConfig loadLayoutFromFile(Path path) {
        try {
            String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            return JSON.parseObject(json, LayoutConfig.class);
        } catch (Exception e) {
            logger.error("解析布局文件失败: {}", path, e);
            return null;
        }
    }
    
    /**
     * 保存布局
     */
    public void saveLayout(LayoutConfig config) {
        try {
            config.setUpdateTime(System.currentTimeMillis());
            
            String fileName = config.getName().replaceAll("[^a-zA-Z0-9_-]", "_") + ".json";
            Path path = layoutPath.resolve(fileName);
            
            String json = JSON.toJSONString(config, true);
            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
            
            layouts.put(config.getName(), config);
            logger.info("保存布局: {}", config.getName());
            
        } catch (IOException e) {
            logger.error("保存布局失败", e);
        }
    }
    
    /**
     * 保存当前布局
     */
    public void saveCurrentLayout(LayoutConfig config) {
        try {
            currentLayout = config;
            
            Path path = layoutPath.resolve(CURRENT_LAYOUT);
            String json = JSON.toJSONString(config, true);
            Files.write(path, json.getBytes(StandardCharsets.UTF_8));
            
        } catch (IOException e) {
            logger.error("保存当前布局失败", e);
        }
    }
    
    /**
     * 从窗口捕获布局
     */
    public LayoutConfig captureFromWindow(JFrame frame, String name) {
        LayoutConfig config = new LayoutConfig(name);
        
        // 窗口位置和大小
        Point location = frame.getLocation();
        config.setWindowX(location.x);
        config.setWindowY(location.y);
        config.setWindowWidth(frame.getWidth());
        config.setWindowHeight(frame.getHeight());
        config.setWindowState(frame.getExtendedState());
        
        // 查找分割面板
        findSplitPanes(frame.getContentPane(), config);
        
        return config;
    }
    
    private void findSplitPanes(Container container, LayoutConfig config) {
        for (Component c : container.getComponents()) {
            if (c instanceof JSplitPane) {
                JSplitPane split = (JSplitPane) c;
                // 根据方向判断是主分割还是内容分割
                if (split.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
                    config.setMainSplitPosition(split.getDividerLocation());
                } else {
                    config.setContentSplitPosition(split.getDividerLocation());
                }
            }
            if (c instanceof Container) {
                findSplitPanes((Container) c, config);
            }
        }
    }
    
    /**
     * 应用布局到窗口
     */
    public void applyToWindow(JFrame frame, LayoutConfig config) {
        if (config == null) return;
        
        // 应用窗口位置和大小
        if (config.getWindowWidth() > 0 && config.getWindowHeight() > 0) {
            frame.setLocation(config.getWindowX(), config.getWindowY());
            frame.setSize(config.getWindowWidth(), config.getWindowHeight());
        }
        
        if (config.getWindowState() != Frame.NORMAL) {
            frame.setExtendedState(config.getWindowState());
        }
        
        // 应用分割面板位置
        SwingUtilities.invokeLater(() -> {
            applySplitPositions(frame.getContentPane(), config);
        });
    }
    
    private void applySplitPositions(Container container, LayoutConfig config) {
        for (Component c : container.getComponents()) {
            if (c instanceof JSplitPane) {
                JSplitPane split = (JSplitPane) c;
                if (split.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
                    if (config.getMainSplitPosition() > 0) {
                        split.setDividerLocation(config.getMainSplitPosition());
                    }
                } else {
                    if (config.getContentSplitPosition() > 0) {
                        split.setDividerLocation(config.getContentSplitPosition());
                    }
                }
            }
            if (c instanceof Container) {
                applySplitPositions((Container) c, config);
            }
        }
    }
    
    /**
     * 删除布局
     */
    public void deleteLayout(String name) {
        layouts.remove(name);
        
        String fileName = name.replaceAll("[^a-zA-Z0-9_-]", "_") + ".json";
        try {
            Files.deleteIfExists(layoutPath.resolve(fileName));
        } catch (IOException e) {
            logger.error("删除布局失败", e);
        }
    }
    
    /**
     * 获取布局
     */
    public LayoutConfig getLayout(String name) {
        return layouts.get(name);
    }
    
    /**
     * 获取当前布局
     */
    public LayoutConfig getCurrentLayout() {
        return currentLayout;
    }
    
    /**
     * 获取所有布局名称
     */
    public List<String> getLayoutNames() {
        return new ArrayList<>(layouts.keySet());
    }
    
    /**
     * 获取所有布局
     */
    public Collection<LayoutConfig> getAllLayouts() {
        return layouts.values();
    }
}
