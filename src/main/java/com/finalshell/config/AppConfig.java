package com.finalshell.config;

/**
 * Application Configuration
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: DataModel_ConfigFormat.md - ClientConfig
 */
public class AppConfig {
    
    // Window settings
    private int windowWidth = 1200;
    private int windowHeight = 800;
    private int windowX = -1;
    private int windowY = -1;
    private boolean windowMaximized = false;
    private int dividerLocation = 250;
    
    // Terminal settings
    private String terminalFont = "Consolas";
    private int terminalFontSize = 14;
    private String terminalTheme = "Default";
    private String terminalCharset = "UTF-8";
    private int terminalScrollback = 10000;
    private int scrollbackLines = 10000;
    private boolean copyOnSelect = true;
    private boolean audibleBell = false;
    
    // SFTP settings
    private boolean sftpShowHidden = false;
    private String sftpDefaultLocalPath = "";
    private int sftpTransferBufferSize = 32768;
    
    // General settings
    private String language = "zh_CN";
    private boolean autoUpdate = true;
    private boolean startMinimized = false;
    private boolean confirmOnClose = true;
    private boolean savePassword = true;
    
    // Proxy settings
    private String proxyType = "NONE";
    private String proxyHost = "";
    private int proxyPort = 0;
    private String proxyUser = "";
    private String proxyPassword = "";
    
    // Theme
    private boolean darkTheme = true;
    private String accentColor = "#0078D7";
    
    // Connection settings
    private boolean autoReconnect = true;
    private int reconnectDelay = 5;
    private boolean confirmExit = true;
    private boolean minimizeToTray = false;
    
    // Terminal size
    private int terminalRows = 24;
    private int terminalCols = 80;
    private boolean cursorBlink = true;
    
    // Transfer settings
    private int maxConcurrentTransfers = 3;
    private int transferBufferSize = 32768;
    private boolean confirmOverwrite = true;
    private boolean preserveTimestamp = true;
    
    // Security settings
    private boolean rememberPassword = true;
    private boolean autoLock = false;
    private int autoLockTime = 30;
    
    public AppConfig() {
    }
    
    // Getters and Setters
    
    public int getWindowWidth() {
        return windowWidth;
    }
    
    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }
    
    public int getWindowHeight() {
        return windowHeight;
    }
    
    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }
    
    public int getWindowX() {
        return windowX;
    }
    
    public void setWindowX(int windowX) {
        this.windowX = windowX;
    }
    
    public int getWindowY() {
        return windowY;
    }
    
    public void setWindowY(int windowY) {
        this.windowY = windowY;
    }
    
    public boolean isWindowMaximized() {
        return windowMaximized;
    }
    
    public void setWindowMaximized(boolean windowMaximized) {
        this.windowMaximized = windowMaximized;
    }
    
    public int getDividerLocation() {
        return dividerLocation;
    }
    
    public void setDividerLocation(int dividerLocation) {
        this.dividerLocation = dividerLocation;
    }
    
    public String getTerminalFont() {
        return terminalFont;
    }
    
    public void setTerminalFont(String terminalFont) {
        this.terminalFont = terminalFont;
    }
    
    public int getTerminalFontSize() {
        return terminalFontSize;
    }
    
    public void setTerminalFontSize(int terminalFontSize) {
        this.terminalFontSize = terminalFontSize;
    }
    
    public String getTerminalTheme() {
        return terminalTheme;
    }
    
    public void setTerminalTheme(String terminalTheme) {
        this.terminalTheme = terminalTheme;
    }
    
    public String getTerminalCharset() {
        return terminalCharset;
    }
    
    public void setTerminalCharset(String terminalCharset) {
        this.terminalCharset = terminalCharset;
    }
    
    public int getTerminalScrollback() {
        return terminalScrollback;
    }
    
    public void setTerminalScrollback(int terminalScrollback) {
        this.terminalScrollback = terminalScrollback;
    }
    
    public boolean isSftpShowHidden() {
        return sftpShowHidden;
    }
    
    public void setSftpShowHidden(boolean sftpShowHidden) {
        this.sftpShowHidden = sftpShowHidden;
    }
    
    public String getSftpDefaultLocalPath() {
        return sftpDefaultLocalPath;
    }
    
    public void setSftpDefaultLocalPath(String sftpDefaultLocalPath) {
        this.sftpDefaultLocalPath = sftpDefaultLocalPath;
    }
    
    public int getSftpTransferBufferSize() {
        return sftpTransferBufferSize;
    }
    
    public void setSftpTransferBufferSize(int sftpTransferBufferSize) {
        this.sftpTransferBufferSize = sftpTransferBufferSize;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public boolean isAutoUpdate() {
        return autoUpdate;
    }
    
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
    
    public boolean isStartMinimized() {
        return startMinimized;
    }
    
    public void setStartMinimized(boolean startMinimized) {
        this.startMinimized = startMinimized;
    }
    
    public boolean isConfirmOnClose() {
        return confirmOnClose;
    }
    
    public void setConfirmOnClose(boolean confirmOnClose) {
        this.confirmOnClose = confirmOnClose;
    }
    
    public boolean isSavePassword() {
        return savePassword;
    }
    
    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }
    
    public String getProxyType() {
        return proxyType;
    }
    
    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }
    
    public String getProxyHost() {
        return proxyHost;
    }
    
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    public int getProxyPort() {
        return proxyPort;
    }
    
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    public String getProxyUser() {
        return proxyUser;
    }
    
    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }
    
    public String getProxyPassword() {
        return proxyPassword;
    }
    
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }
    
    public boolean isDarkTheme() {
        return darkTheme;
    }
    
    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }
    
    public String getAccentColor() {
        return accentColor;
    }
    
    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }
    
    public int getScrollbackLines() {
        return scrollbackLines;
    }
    
    public void setScrollbackLines(int scrollbackLines) {
        this.scrollbackLines = scrollbackLines;
    }
    
    public boolean isCopyOnSelect() {
        return copyOnSelect;
    }
    
    public void setCopyOnSelect(boolean copyOnSelect) {
        this.copyOnSelect = copyOnSelect;
    }
    
    public boolean isAudibleBell() {
        return audibleBell;
    }
    
    public void setAudibleBell(boolean audibleBell) {
        this.audibleBell = audibleBell;
    }
    
    // Connection settings
    public boolean isAutoReconnect() { return autoReconnect; }
    public void setAutoReconnect(boolean autoReconnect) { this.autoReconnect = autoReconnect; }
    
    public int getReconnectDelay() { return reconnectDelay; }
    public void setReconnectDelay(int reconnectDelay) { this.reconnectDelay = reconnectDelay; }
    
    public boolean isConfirmExit() { return confirmExit; }
    public void setConfirmExit(boolean confirmExit) { this.confirmExit = confirmExit; }
    
    public boolean isMinimizeToTray() { return minimizeToTray; }
    public void setMinimizeToTray(boolean minimizeToTray) { this.minimizeToTray = minimizeToTray; }
    
    // Terminal size
    public int getTerminalRows() { return terminalRows; }
    public void setTerminalRows(int terminalRows) { this.terminalRows = terminalRows; }
    
    public int getTerminalCols() { return terminalCols; }
    public void setTerminalCols(int terminalCols) { this.terminalCols = terminalCols; }
    
    public boolean isCursorBlink() { return cursorBlink; }
    public void setCursorBlink(boolean cursorBlink) { this.cursorBlink = cursorBlink; }
    
    // Transfer settings
    public int getMaxConcurrentTransfers() { return maxConcurrentTransfers; }
    public void setMaxConcurrentTransfers(int maxConcurrentTransfers) { this.maxConcurrentTransfers = maxConcurrentTransfers; }
    
    public int getTransferBufferSize() { return transferBufferSize; }
    public void setTransferBufferSize(int transferBufferSize) { this.transferBufferSize = transferBufferSize; }
    
    public boolean isConfirmOverwrite() { return confirmOverwrite; }
    public void setConfirmOverwrite(boolean confirmOverwrite) { this.confirmOverwrite = confirmOverwrite; }
    
    public boolean isPreserveTimestamp() { return preserveTimestamp; }
    public void setPreserveTimestamp(boolean preserveTimestamp) { this.preserveTimestamp = preserveTimestamp; }
    
    // Security settings
    public boolean isRememberPassword() { return rememberPassword; }
    public void setRememberPassword(boolean rememberPassword) { this.rememberPassword = rememberPassword; }
    
    public boolean isAutoLock() { return autoLock; }
    public void setAutoLock(boolean autoLock) { this.autoLock = autoLock; }
    
    public int getAutoLockTime() { return autoLockTime; }
    public void setAutoLockTime(int autoLockTime) { this.autoLockTime = autoLockTime; }
}
