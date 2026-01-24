package com.finalshell.app;

import com.finalshell.config.AppConfig;
import com.finalshell.config.ConfigManager;
import com.finalshell.ui.MainWindow;
import com.finalshell.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * FinalShell Clone - Application Entry Point
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: App_Analysis.md, Implementation_Guide.md
 */
public class App {
    
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    
    private static App instance;
    private MainWindow mainWindow;
    private ConfigManager configManager;
    private AppConfig appConfig;
    
    public static void main(String[] args) {
        logger.info("FinalShell Clone starting...");
        
        try {
            // Set system properties for better rendering
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            
            // Initialize application on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    getInstance().start();
                } catch (Exception e) {
                    logger.error("Failed to start application", e);
                    showErrorDialog("启动失败", e.getMessage());
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            logger.error("Application initialization failed", e);
            System.exit(1);
        }
    }
    
    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }
    
    public static com.finalshell.control.ControlClient getControlClient() {
        return com.finalshell.control.ControlClient.getInstance();
    }
    
    private App() {
        // Private constructor for singleton
    }
    
    public void start() throws Exception {
        logger.info("Initializing application...");
        
        // Step 1: Initialize configuration manager
        configManager = ConfigManager.getInstance();
        appConfig = configManager.getAppConfig();
        logger.info("Configuration loaded from: {}", configManager.getConfigDir());
        
        // Step 2: Initialize resource loader
        ResourceLoader.getInstance().init();
        logger.info("Resources initialized");
        
        // Step 3: Set Look and Feel
        initLookAndFeel();
        
        // Step 4: Create and show main window
        mainWindow = new MainWindow();
        mainWindow.setVisible(true);
        
        logger.info("Application started successfully");
    }
    
    private void initLookAndFeel() {
        try {
            // Try to use WebLaF
            UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel");
            logger.info("WebLaF Look and Feel applied");
        } catch (Exception e) {
            logger.warn("WebLaF not available, using system L&F: {}", e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                logger.error("Failed to set Look and Feel", ex);
            }
        }
    }
    
    private static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public MainWindow getMainWindow() {
        return mainWindow;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public AppConfig getAppConfig() {
        return appConfig;
    }
    
    public void exit() {
        logger.info("Application exiting...");
        
        // Save configuration
        if (configManager != null) {
            configManager.saveAll();
        }
        
        // Dispose main window
        if (mainWindow != null) {
            mainWindow.dispose();
        }
        
        logger.info("Application shutdown complete");
        System.exit(0);
    }
}
