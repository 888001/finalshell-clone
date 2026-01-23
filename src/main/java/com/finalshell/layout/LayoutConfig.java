package com.finalshell.layout;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 窗口布局配置
 */
public class LayoutConfig {
    private String name;
    private String description;
    private long createTime;
    private long updateTime;
    
    // 主窗口
    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;
    private int windowState; // Frame.NORMAL, Frame.MAXIMIZED_BOTH
    
    // 分割面板位置
    private int mainSplitPosition;      // 主分割(左侧树 | 右侧内容)
    private int contentSplitPosition;   // 内容分割(终端 | 下方面板)
    
    // 面板可见性
    private boolean treeVisible = true;
    private boolean monitorVisible = true;
    private boolean sftpVisible = true;
    private boolean toolbarVisible = true;
    private boolean statusBarVisible = true;
    
    // 标签页状态
    private List<TabState> openTabs = new ArrayList<>();
    private int activeTabIndex;
    
    public LayoutConfig() {
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }
    
    public LayoutConfig(String name) {
        this();
        this.name = name;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
    
    public int getWindowX() { return windowX; }
    public void setWindowX(int windowX) { this.windowX = windowX; }
    
    public int getWindowY() { return windowY; }
    public void setWindowY(int windowY) { this.windowY = windowY; }
    
    public int getWindowWidth() { return windowWidth; }
    public void setWindowWidth(int windowWidth) { this.windowWidth = windowWidth; }
    
    public int getWindowHeight() { return windowHeight; }
    public void setWindowHeight(int windowHeight) { this.windowHeight = windowHeight; }
    
    public int getWindowState() { return windowState; }
    public void setWindowState(int windowState) { this.windowState = windowState; }
    
    public int getMainSplitPosition() { return mainSplitPosition; }
    public void setMainSplitPosition(int mainSplitPosition) { this.mainSplitPosition = mainSplitPosition; }
    
    public int getContentSplitPosition() { return contentSplitPosition; }
    public void setContentSplitPosition(int contentSplitPosition) { this.contentSplitPosition = contentSplitPosition; }
    
    public boolean isTreeVisible() { return treeVisible; }
    public void setTreeVisible(boolean treeVisible) { this.treeVisible = treeVisible; }
    
    public boolean isMonitorVisible() { return monitorVisible; }
    public void setMonitorVisible(boolean monitorVisible) { this.monitorVisible = monitorVisible; }
    
    public boolean isSftpVisible() { return sftpVisible; }
    public void setSftpVisible(boolean sftpVisible) { this.sftpVisible = sftpVisible; }
    
    public boolean isToolbarVisible() { return toolbarVisible; }
    public void setToolbarVisible(boolean toolbarVisible) { this.toolbarVisible = toolbarVisible; }
    
    public boolean isStatusBarVisible() { return statusBarVisible; }
    public void setStatusBarVisible(boolean statusBarVisible) { this.statusBarVisible = statusBarVisible; }
    
    public List<TabState> getOpenTabs() { return openTabs; }
    public void setOpenTabs(List<TabState> openTabs) { this.openTabs = openTabs; }
    
    public int getActiveTabIndex() { return activeTabIndex; }
    public void setActiveTabIndex(int activeTabIndex) { this.activeTabIndex = activeTabIndex; }
    
    /**
     * 标签页状态
     */
    public static class TabState {
        private String connectionId;
        private String tabType;  // ssh, sftp, vnc, telnet, ftp, etc.
        private String title;
        
        public TabState() {}
        
        public TabState(String connectionId, String tabType, String title) {
            this.connectionId = connectionId;
            this.tabType = tabType;
            this.title = title;
        }
        
        public String getConnectionId() { return connectionId; }
        public void setConnectionId(String connectionId) { this.connectionId = connectionId; }
        
        public String getTabType() { return tabType; }
        public void setTabType(String tabType) { this.tabType = tabType; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
}
