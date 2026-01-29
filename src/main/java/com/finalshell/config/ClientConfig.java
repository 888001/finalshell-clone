package com.finalshell.config;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户端配置
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ClientConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // 基本信息
    private String clientId;
    private String clientName;
    private String version;
    private String language;
    
    // 主题和外观
    private String theme = "Default";
    private String fontName;
    private String fontNameCN;
    private int fontSize = 12;
    private String backgroundImage = "bg1.jpg";
    private int backgroundOpacity = 4;
    
    // 窗口状态
    private Rectangle windowBounds;
    private boolean windowMaximized = false;
    private int treePanelWidth = 250;
    private int monitorPanelHeight = 140;
    private int sftpDividerLocation = 600;
    
    // 路径设置
    private String downloadPath;
    private String uploadPath;
    private String lastSelectKeyPath;
    
    // 最近列表
    private List<String> recentDownloadPaths = new ArrayList<>();
    private List<String> recentUploadPaths = new ArrayList<>();
    private List<String> recentFilePaths = new ArrayList<>();
    private List<String> recentCommands = new ArrayList<>();
    private List<String> recentSearchCommands = new ArrayList<>();
    private List<String> recentGrepCommands = new ArrayList<>();
    private List<String> recentHostPaths = new ArrayList<>();
    
    // 快捷键配置
    private Map<Integer, HotkeyEntry> hotkeys = new HashMap<>();
    
    // 功能开关
    private boolean autoUpdate = true;
    private boolean showWelcome = true;
    private boolean showSidebar = true;
    private boolean showStatusBar = true;
    private boolean showToolbar = true;
    private boolean confirmOnClose = true;
    private boolean confirmOnCloseTab = true;
    private boolean enableSound = true;
    private boolean enableNotification = true;
    private boolean autoReconnect = true;
    private boolean rememberPassword = true;
    
    // 终端设置
    private int terminalScrollBuffer = 200;
    private boolean terminalBlink = true;
    private boolean terminalBell = true;
    
    // 连接设置
    private int maxConnections = 10;
    private int sessionTimeout = 30;
    private int keepAliveInterval = 60;
    
    // 传输设置
    public static final int TRANSFER_MODE_BINARY = 1;
    public static final int TRANSFER_MODE_ASCII = 2;
    private int transferMode = TRANSFER_MODE_BINARY;
    
    // 双击行为
    public static final int DOUBLE_CLICK_EDIT = 1;
    public static final int DOUBLE_CLICK_DOWNLOAD = 2;
    private int doubleClickAction = DOUBLE_CLICK_EDIT;
    
    // 排序方式
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_SIZE = 2;
    public static final int SORT_BY_TIME = 3;
    public static final int SORT_BY_TYPE = 4;
    private int localSortType = SORT_BY_NAME;
    private int remoteSortType = SORT_BY_NAME;
    
    // 同步设置
    private String syncEmail;
    private String syncToken;
    private boolean syncEnabled = false;
    
    public ClientConfig() {
        this.language = "zh_CN";
        initDefaultHotkeys();
    }
    
    private void initDefaultHotkeys() {
        // 终端快捷键
        addHotkey(1, "终端 - 复制", 192, 67);  // Ctrl+Shift+C
        addHotkey(2, "终端 - 粘贴", 192, 86);  // Ctrl+Shift+V
        addHotkey(3, "终端 - 查找", 192, 70);  // Ctrl+Shift+F
        addHotkey(4, "终端 - 全屏", 512, 10);  // Cmd/Ctrl+Enter
        addHotkey(5, "终端 - 重连", 0, 10);    // Enter
        addHotkey(6, "终端 - 放大", 512, 61);  // Cmd/Ctrl+=
        addHotkey(7, "终端 - 缩小", 512, 45);  // Cmd/Ctrl+-
        addHotkey(8, "终端 - 缩放重置", 512, 48); // Cmd/Ctrl+0
        addHotkey(9, "上一标签", 512, 48);     // Cmd/Ctrl+[
        addHotkey(10, "下一标签", 512, 93);    // Cmd/Ctrl+]
        addHotkey(11, "关闭标签", 512, 81);    // Cmd/Ctrl+Q
        addHotkey(12, "连接管理器", 512, 65);  // Cmd/Ctrl+A
    }
    
    private void addHotkey(int id, String name, int modifiers, int keyCode) {
        hotkeys.put(id, new HotkeyEntry(id, name, modifiers, keyCode));
    }
    
    public HotkeyEntry getHotkey(int id) {
        return hotkeys.get(id);
    }
    
    public void setHotkey(int id, int modifiers, int keyCode) {
        HotkeyEntry entry = hotkeys.get(id);
        if (entry != null) {
            entry.setModifiers(modifiers);
            entry.setKeyCode(keyCode);
        }
    }
    
    public Map<Integer, HotkeyEntry> getHotkeys() {
        return hotkeys;
    }
    
    // 基本信息 Getter/Setter
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    // 主题和外观 Getter/Setter
    
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    
    public String getFontName() { return fontName; }
    public void setFontName(String fontName) { this.fontName = fontName; }
    
    public String getFontNameCN() { return fontNameCN; }
    public void setFontNameCN(String fontNameCN) { this.fontNameCN = fontNameCN; }
    
    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    
    public String getBackgroundImage() { return backgroundImage; }
    public void setBackgroundImage(String backgroundImage) { this.backgroundImage = backgroundImage; }
    
    public int getBackgroundOpacity() { return backgroundOpacity; }
    public void setBackgroundOpacity(int backgroundOpacity) { this.backgroundOpacity = backgroundOpacity; }
    
    // 窗口状态 Getter/Setter
    
    public Rectangle getWindowBounds() { return windowBounds; }
    public void setWindowBounds(Rectangle windowBounds) { this.windowBounds = windowBounds; }
    
    public boolean isWindowMaximized() { return windowMaximized; }
    public void setWindowMaximized(boolean windowMaximized) { this.windowMaximized = windowMaximized; }
    
    public int getTreePanelWidth() { return treePanelWidth; }
    public void setTreePanelWidth(int treePanelWidth) { this.treePanelWidth = treePanelWidth; }
    
    public int getMonitorPanelHeight() { return monitorPanelHeight; }
    public void setMonitorPanelHeight(int monitorPanelHeight) { this.monitorPanelHeight = monitorPanelHeight; }
    
    public int getSftpDividerLocation() { return sftpDividerLocation; }
    public void setSftpDividerLocation(int sftpDividerLocation) { this.sftpDividerLocation = sftpDividerLocation; }
    
    // 路径设置 Getter/Setter
    
    public String getDownloadPath() { return downloadPath; }
    public void setDownloadPath(String downloadPath) { this.downloadPath = downloadPath; }
    
    public String getUploadPath() { return uploadPath; }
    public void setUploadPath(String uploadPath) { this.uploadPath = uploadPath; }
    
    public String getLastSelectKeyPath() { return lastSelectKeyPath; }
    public void setLastSelectKeyPath(String lastSelectKeyPath) { this.lastSelectKeyPath = lastSelectKeyPath; }
    
    // 最近列表 Getter/Setter
    
    public List<String> getRecentDownloadPaths() { return recentDownloadPaths; }
    public void setRecentDownloadPaths(List<String> recentDownloadPaths) { this.recentDownloadPaths = recentDownloadPaths; }
    
    public List<String> getRecentUploadPaths() { return recentUploadPaths; }
    public void setRecentUploadPaths(List<String> recentUploadPaths) { this.recentUploadPaths = recentUploadPaths; }
    
    public List<String> getRecentFilePaths() { return recentFilePaths; }
    public void setRecentFilePaths(List<String> recentFilePaths) { this.recentFilePaths = recentFilePaths; }
    
    public List<String> getRecentCommands() { return recentCommands; }
    public void setRecentCommands(List<String> recentCommands) { this.recentCommands = recentCommands; }
    
    public List<String> getRecentSearchCommands() { return recentSearchCommands; }
    public void setRecentSearchCommands(List<String> recentSearchCommands) { this.recentSearchCommands = recentSearchCommands; }
    
    public List<String> getRecentGrepCommands() { return recentGrepCommands; }
    public void setRecentGrepCommands(List<String> recentGrepCommands) { this.recentGrepCommands = recentGrepCommands; }
    
    public List<String> getRecentHostPaths() { return recentHostPaths; }
    public void setRecentHostPaths(List<String> recentHostPaths) { this.recentHostPaths = recentHostPaths; }
    
    // 功能开关 Getter/Setter
    
    public boolean isAutoUpdate() { return autoUpdate; }
    public void setAutoUpdate(boolean autoUpdate) { this.autoUpdate = autoUpdate; }
    
    public boolean isShowWelcome() { return showWelcome; }
    public void setShowWelcome(boolean showWelcome) { this.showWelcome = showWelcome; }
    
    public boolean isShowSidebar() { return showSidebar; }
    public void setShowSidebar(boolean showSidebar) { this.showSidebar = showSidebar; }
    
    public boolean isShowStatusBar() { return showStatusBar; }
    public void setShowStatusBar(boolean showStatusBar) { this.showStatusBar = showStatusBar; }
    
    public boolean isShowToolbar() { return showToolbar; }
    public void setShowToolbar(boolean showToolbar) { this.showToolbar = showToolbar; }
    
    public boolean isConfirmOnClose() { return confirmOnClose; }
    public void setConfirmOnClose(boolean confirmOnClose) { this.confirmOnClose = confirmOnClose; }
    
    public boolean isConfirmOnCloseTab() { return confirmOnCloseTab; }
    public void setConfirmOnCloseTab(boolean confirmOnCloseTab) { this.confirmOnCloseTab = confirmOnCloseTab; }
    
    public boolean isEnableSound() { return enableSound; }
    public void setEnableSound(boolean enableSound) { this.enableSound = enableSound; }
    
    public boolean isEnableNotification() { return enableNotification; }
    public void setEnableNotification(boolean enableNotification) { this.enableNotification = enableNotification; }
    
    public boolean isAutoReconnect() { return autoReconnect; }
    public void setAutoReconnect(boolean autoReconnect) { this.autoReconnect = autoReconnect; }
    
    public boolean isRememberPassword() { return rememberPassword; }
    public void setRememberPassword(boolean rememberPassword) { this.rememberPassword = rememberPassword; }
    
    // 终端设置 Getter/Setter
    
    public int getTerminalScrollBuffer() { return terminalScrollBuffer; }
    public void setTerminalScrollBuffer(int terminalScrollBuffer) { this.terminalScrollBuffer = terminalScrollBuffer; }
    
    public boolean isTerminalBlink() { return terminalBlink; }
    public void setTerminalBlink(boolean terminalBlink) { this.terminalBlink = terminalBlink; }
    
    public boolean isTerminalBell() { return terminalBell; }
    public void setTerminalBell(boolean terminalBell) { this.terminalBell = terminalBell; }
    
    // 连接设置 Getter/Setter
    
    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
    
    public int getSessionTimeout() { return sessionTimeout; }
    public void setSessionTimeout(int sessionTimeout) { this.sessionTimeout = sessionTimeout; }
    
    public int getKeepAliveInterval() { return keepAliveInterval; }
    public void setKeepAliveInterval(int keepAliveInterval) { this.keepAliveInterval = keepAliveInterval; }
    
    // 传输设置 Getter/Setter
    
    public int getTransferMode() { return transferMode; }
    public void setTransferMode(int transferMode) { this.transferMode = transferMode; }
    
    public int getDoubleClickAction() { return doubleClickAction; }
    public void setDoubleClickAction(int doubleClickAction) { this.doubleClickAction = doubleClickAction; }
    
    public int getLocalSortType() { return localSortType; }
    public void setLocalSortType(int localSortType) { this.localSortType = localSortType; }
    
    public int getRemoteSortType() { return remoteSortType; }
    public void setRemoteSortType(int remoteSortType) { this.remoteSortType = remoteSortType; }
    
    // 同步设置 Getter/Setter
    
    public String getSyncEmail() { return syncEmail; }
    public void setSyncEmail(String syncEmail) { this.syncEmail = syncEmail; }
    
    public String getSyncToken() { return syncToken; }
    public void setSyncToken(String syncToken) { this.syncToken = syncToken; }
    
    public boolean isSyncEnabled() { return syncEnabled; }
    public void setSyncEnabled(boolean syncEnabled) { this.syncEnabled = syncEnabled; }
    
    // 最近列表辅助方法
    
    public void addRecentDownloadPath(String path) {
        addToRecentList(recentDownloadPaths, path, 10);
    }
    
    public void addRecentUploadPath(String path) {
        addToRecentList(recentUploadPaths, path, 10);
    }
    
    public void addRecentCommand(String cmd) {
        addToRecentList(recentCommands, cmd, 50);
    }
    
    private void addToRecentList(List<String> list, String item, int maxSize) {
        list.remove(item);
        list.add(0, item);
        while (list.size() > maxSize) {
            list.remove(list.size() - 1);
        }
    }
    
    /**
     * 快捷键配置项
     */
    public static class HotkeyEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private int id;
        private String name;
        private int modifiers;
        private int keyCode;
        
        public HotkeyEntry() {}
        
        public HotkeyEntry(int id, String name, int modifiers, int keyCode) {
            this.id = id;
            this.name = name;
            this.modifiers = modifiers;
            this.keyCode = keyCode;
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public int getModifiers() { return modifiers; }
        public void setModifiers(int modifiers) { this.modifiers = modifiers; }
        
        public int getKeyCode() { return keyCode; }
        public void setKeyCode(int keyCode) { this.keyCode = keyCode; }
    }
}
