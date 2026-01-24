package com.finalshell.ui;

import java.util.*;

/**
 * 主窗口管理器 - 多窗口管理
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md - MainWindowManager
 */
public class MainWindowManager {
    
    private final Map<Integer, MainWindow> windowMap = new HashMap<>();
    private final List<MainWindow> windowList = new ArrayList<>();
    private MainWindow currentWindow;
    private int nextWindowId = 1;
    
    private static MainWindowManager instance;
    
    MainWindowManager() {}
    
    public static synchronized MainWindowManager getInstance() {
        if (instance == null) {
            instance = new MainWindowManager();
        }
        return instance;
    }
    
    /**
     * 创建新窗口
     */
    public MainWindow createWindow() {
        return createWindow(true);
    }
    
    /**
     * 创建新窗口
     */
    public MainWindow createWindow(boolean visible) {
        MainWindow window = new MainWindow();
        int windowId = nextWindowId++;
        
        windowMap.put(windowId, window);
        windowList.add(window);
        currentWindow = window;
        
        if (visible) {
            window.setVisible(true);
        }
        
        return window;
    }
    
    /**
     * 移除窗口
     */
    public void removeWindow(MainWindow window) {
        Integer keyToRemove = null;
        for (Map.Entry<Integer, MainWindow> entry : windowMap.entrySet()) {
            if (entry.getValue() == window) {
                keyToRemove = entry.getKey();
                break;
            }
        }
        
        if (keyToRemove != null) {
            windowMap.remove(keyToRemove);
        }
        windowList.remove(window);
        
        // 更新当前窗口
        if (currentWindow == window) {
            currentWindow = windowList.isEmpty() ? null : windowList.get(windowList.size() - 1);
        }
    }
    
    /**
     * 获取窗口数量
     */
    public int getWindowCount() {
        return windowList.size();
    }
    
    /**
     * 批量执行操作
     */
    public void batchExecute(BatchExecutable executable) {
        for (MainWindow window : windowList) {
            executable.execute(window);
        }
    }
    
    /**
     * 刷新所有窗口
     */
    public void refreshAllWindows() {
        for (MainWindow window : windowList) {
            window.repaint();
        }
    }
    
    /**
     * 获取当前窗口
     */
    public MainWindow getCurrentWindow() {
        return currentWindow;
    }
    
    /**
     * 获取最后一个窗口
     */
    public MainWindow getLastWindow() {
        return windowList.isEmpty() ? null : windowList.get(windowList.size() - 1);
    }
    
    /**
     * 设置当前窗口
     */
    public void setCurrentWindow(MainWindow window) {
        if (windowList.contains(window)) {
            this.currentWindow = window;
        }
    }
    
    /**
     * 获取所有窗口
     */
    public List<MainWindow> getAllWindows() {
        return new ArrayList<>(windowList);
    }
    
    /**
     * 关闭所有窗口
     */
    public void closeAllWindows() {
        for (MainWindow window : new ArrayList<>(windowList)) {
            window.dispose();
        }
        windowList.clear();
        windowMap.clear();
        currentWindow = null;
    }
    
    /**
     * 初始化
     */
    public void initialize() {
        // 初始化默认配置
    }
    
    /**
     * 显示指定名称的窗口
     */
    public void showWindow(String name) {
        for (MainWindow window : windowList) {
            if (name != null && name.equals(window.getName())) {
                window.setVisible(true);
                window.toFront();
                setCurrentWindow(window);
                return;
            }
        }
        // 如果没有找到，创建新窗口
        MainWindow window = createWindow();
        window.setName(name);
    }
    
    /**
     * 显示默认窗口
     */
    public void showDefaultWindow() {
        if (windowList.isEmpty()) {
            createWindow();
        } else {
            MainWindow window = windowList.get(0);
            window.setVisible(true);
            window.toFront();
            setCurrentWindow(window);
        }
    }
    
    /**
     * 批量执行接口
     */
    public interface BatchExecutable {
        void execute(MainWindow window);
    }
}
