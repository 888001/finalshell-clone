package com.finalshell.ui;

import java.io.Serializable;

/**
 * 布局配置
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - LayoutConfig
 */
public class LayoutConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int mainDividerLocation = 200;
    private int terminalDividerLocation = 300;
    private int sftpDividerLocation = 400;
    private int windowX = 100;
    private int windowY = 100;
    private int windowWidth = 1200;
    private int windowHeight = 800;
    private boolean maximized = false;
    private boolean leftPanelVisible = true;
    private boolean bottomPanelVisible = true;
    private int leftPanelWidth = 200;
    private int bottomPanelHeight = 200;
    
    public LayoutConfig() {}
    
    public void copyFrom(LayoutConfig other) {
        this.mainDividerLocation = other.mainDividerLocation;
        this.terminalDividerLocation = other.terminalDividerLocation;
        this.sftpDividerLocation = other.sftpDividerLocation;
        this.windowX = other.windowX;
        this.windowY = other.windowY;
        this.windowWidth = other.windowWidth;
        this.windowHeight = other.windowHeight;
        this.maximized = other.maximized;
        this.leftPanelVisible = other.leftPanelVisible;
        this.bottomPanelVisible = other.bottomPanelVisible;
        this.leftPanelWidth = other.leftPanelWidth;
        this.bottomPanelHeight = other.bottomPanelHeight;
    }
    
    // Getters and Setters
    public int getMainDividerLocation() { return mainDividerLocation; }
    public void setMainDividerLocation(int mainDividerLocation) { 
        this.mainDividerLocation = mainDividerLocation; 
    }
    
    public int getTerminalDividerLocation() { return terminalDividerLocation; }
    public void setTerminalDividerLocation(int terminalDividerLocation) { 
        this.terminalDividerLocation = terminalDividerLocation; 
    }
    
    public int getSftpDividerLocation() { return sftpDividerLocation; }
    public void setSftpDividerLocation(int sftpDividerLocation) { 
        this.sftpDividerLocation = sftpDividerLocation; 
    }
    
    public int getWindowX() { return windowX; }
    public void setWindowX(int windowX) { this.windowX = windowX; }
    
    public int getWindowY() { return windowY; }
    public void setWindowY(int windowY) { this.windowY = windowY; }
    
    public int getWindowWidth() { return windowWidth; }
    public void setWindowWidth(int windowWidth) { this.windowWidth = windowWidth; }
    
    public int getWindowHeight() { return windowHeight; }
    public void setWindowHeight(int windowHeight) { this.windowHeight = windowHeight; }
    
    public boolean isMaximized() { return maximized; }
    public void setMaximized(boolean maximized) { this.maximized = maximized; }
    
    public boolean isLeftPanelVisible() { return leftPanelVisible; }
    public void setLeftPanelVisible(boolean leftPanelVisible) { 
        this.leftPanelVisible = leftPanelVisible; 
    }
    
    public boolean isBottomPanelVisible() { return bottomPanelVisible; }
    public void setBottomPanelVisible(boolean bottomPanelVisible) { 
        this.bottomPanelVisible = bottomPanelVisible; 
    }
    
    public int getLeftPanelWidth() { return leftPanelWidth; }
    public void setLeftPanelWidth(int leftPanelWidth) { this.leftPanelWidth = leftPanelWidth; }
    
    public int getBottomPanelHeight() { return bottomPanelHeight; }
    public void setBottomPanelHeight(int bottomPanelHeight) { 
        this.bottomPanelHeight = bottomPanelHeight; 
    }
}
