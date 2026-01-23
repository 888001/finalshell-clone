package com.finalshell.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * 系统托盘管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SystemTrayManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemTrayManager.class);
    
    private static SystemTrayManager instance;
    
    private TrayIcon trayIcon;
    private boolean initialized = false;
    private MainWindow mainWindow;
    
    public static synchronized SystemTrayManager getInstance() {
        if (instance == null) {
            instance = new SystemTrayManager();
        }
        return instance;
    }
    
    private SystemTrayManager() {}
    
    public void initialize(MainWindow window) {
        this.mainWindow = window;
        
        if (!SystemTray.isSupported()) {
            logger.warn("System tray is not supported");
            return;
        }
        
        try {
            // 加载图标
            Image image = loadTrayIcon();
            if (image == null) {
                logger.warn("Failed to load tray icon");
                return;
            }
            
            // 创建托盘图标
            trayIcon = new TrayIcon(image, "FinalShell Clone");
            trayIcon.setImageAutoSize(true);
            
            // 双击显示窗口
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        showMainWindow();
                    }
                }
            });
            
            // 右键菜单
            PopupMenu popup = createPopupMenu();
            trayIcon.setPopupMenu(popup);
            
            // 添加到系统托盘
            SystemTray.getSystemTray().add(trayIcon);
            initialized = true;
            
            logger.info("System tray initialized");
            
        } catch (Exception e) {
            logger.error("Failed to initialize system tray", e);
        }
    }
    
    private Image loadTrayIcon() {
        try {
            URL iconUrl = getClass().getResource("/images/icon.png");
            if (iconUrl != null) {
                return new ImageIcon(iconUrl).getImage();
            }
            
            // 创建默认图标
            int size = 16;
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(70, 130, 180));
            g.fillOval(1, 1, size - 2, size - 2);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            g.drawString("F", 4, 12);
            g.dispose();
            return img;
            
        } catch (Exception e) {
            logger.error("Failed to load tray icon", e);
            return null;
        }
    }
    
    private PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu();
        
        // 显示主窗口
        MenuItem showItem = new MenuItem("显示主窗口");
        showItem.addActionListener(e -> showMainWindow());
        popup.add(showItem);
        
        popup.addSeparator();
        
        // 新建连接
        MenuItem newConnItem = new MenuItem("新建连接");
        newConnItem.addActionListener(e -> {
            showMainWindow();
            mainWindow.showNewConnectionDialog();
        });
        popup.add(newConnItem);
        
        popup.addSeparator();
        
        // 退出
        MenuItem exitItem = new MenuItem("退出");
        exitItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(null,
                "确定要退出程序吗?",
                "确认",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                removeTrayIcon();
                mainWindow.dispose();
                System.exit(0);
            }
        });
        popup.add(exitItem);
        
        return popup;
    }
    
    public void showMainWindow() {
        if (mainWindow != null) {
            mainWindow.setVisible(true);
            mainWindow.setExtendedState(JFrame.NORMAL);
            mainWindow.toFront();
            mainWindow.requestFocus();
        }
    }
    
    public void hideToTray() {
        if (initialized && mainWindow != null) {
            mainWindow.setVisible(false);
            showNotification("FinalShell Clone", "程序已最小化到托盘");
        }
    }
    
    public void showNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }
    
    public void showErrorNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.ERROR);
        }
    }
    
    public void removeTrayIcon() {
        if (trayIcon != null && initialized) {
            SystemTray.getSystemTray().remove(trayIcon);
            initialized = false;
        }
    }
    
    public boolean isInitialized() {
        return initialized;
    }
}
