package com.finalshell.app;

import com.finalshell.config.AppConfig;
import com.finalshell.config.ClientConfig;
import com.finalshell.config.ConfigManager;
import com.finalshell.config.FontConfigManager;
import com.finalshell.control.CheckThread;
import com.finalshell.history.HistoryManager;
import com.finalshell.hotkey.HotkeyManager;
import com.finalshell.key.SecretKeyManager;
import com.finalshell.proxy.ProxyManager;
import com.finalshell.sync.DeleteManager;
import com.finalshell.sync.SyncManager;
import com.finalshell.terminal.QuickCommandManager;
import com.finalshell.theme.ThemeManager;
import com.finalshell.thread.ThreadManager;
import com.finalshell.transfer.TransTaskManager;
import com.finalshell.ui.ImageManager;
import com.finalshell.ui.LayoutConfigManager;
import com.finalshell.ui.MainWindow;
import com.finalshell.ui.SystemTrayManager;
import com.finalshell.util.DesUtil;
import com.finalshell.util.OSDetector;
import com.finalshell.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

/**
 * FinalShell Clone - Application Entry Point
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: App_Analysis.md, Implementation_Guide.md
 */
public class App {
    
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final int BASE_PORT = 30578;
    private static final int PORT_RANGE = 10;
    
    private static App instance;
    private static boolean startMinimized = false;
    public static long startTime;
    public static ScheduledExecutorService scheduledExecutor;
    public static HashSet<String> systemFontNames = new HashSet<>();
    
    // Core managers
    private MainWindow mainWindow;
    private ConfigManager configManager;
    private AppConfig appConfig;
    private ClientConfig clientConfig;
    
    // Feature managers
    private ThreadManager threadManager;
    private ThemeManager themeManager;
    private FontConfigManager fontConfigManager;
    private ImageManager imageManager;
    private ProxyManager proxyManager;
    private SecretKeyManager secretKeyManager;
    private HotkeyManager hotkeyManager;
    private TransTaskManager transTaskManager;
    private DeleteManager deleteManager;
    private SyncManager syncManager;
    private LayoutConfigManager layoutConfigManager;
    private QuickCommandManager quickCommandManager;
    private SystemTrayManager systemTrayManager;
    private Thread controlCheckThread;
    
    // Process detection
    private DatagramSocket monitorSocket;
    private boolean processExists = false;
    private boolean isShuttingDown = false;
    
    // App listeners
    private List<AppListener> appListeners = new ArrayList<>();
    
    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        logger.info("FinalShell Clone starting...");
        
        // Parse command line arguments
        for (String arg : args) {
            if ("-min".equals(arg)) {
                startMinimized = true;
                logger.info("Starting minimized");
            }
        }
        
        // Set system properties for better rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        
        // Set application name for Linux
        if (OSDetector.isLinux()) {
            try {
                Toolkit xToolkit = Toolkit.getDefaultToolkit();
                java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
                awtAppClassNameField.setAccessible(true);
                awtAppClassNameField.set(xToolkit, "FinalShell");
            } catch (Exception e) {
                logger.debug("Could not set app name for Linux", e);
            }
        }
        
        // Initialize static resources
        scheduledExecutor = Executors.newScheduledThreadPool(10);
        
        // Create and start application
        instance = new App();
        instance.initialize();
    }
    
    public static App getInstance() {
        return instance;
    }
    
    public static com.finalshell.control.ControlClient getControlClient() {
        return com.finalshell.control.ControlClient.getInstance();
    }
    
    private App() {
        // Private constructor for singleton
        this.clientConfig = new ClientConfig();
        this.layoutConfigManager = LayoutConfigManager.getInstance();
    }
    
    /**
     * Initialize the application
     */
    private void initialize() {
        // Check for existing process
        checkExistingProcess();
        
        logger.info("Initializing managers...");
        
        // Load system fonts
        loadSystemFonts();
        
        // Initialize core managers
        threadManager = ThreadManager.getInstance();
        imageManager = ImageManager.getInstance();
        configManager = ConfigManager.getInstance();
        configManager.init();
        appConfig = configManager.getAppConfig();
        
        // Initialize feature managers
        themeManager = new ThemeManager();
        themeManager.init();
        fontConfigManager = new FontConfigManager();
        proxyManager = ProxyManager.getInstance();
        secretKeyManager = SecretKeyManager.getInstance();
        hotkeyManager = HotkeyManager.getInstance();
        transTaskManager = TransTaskManager.getInstance();
        quickCommandManager = QuickCommandManager.getInstance();
        
        logger.info("Configuration loaded from: {}", configManager.getConfigDir());
        
        // Load client config
        loadClientConfig();
        
        // Initialize resource loader
        ResourceLoader.getInstance().init();
        logger.info("Resources initialized");
        
        // Set Look and Feel on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                initLookAndFeel();
                
                // Create main window
                mainWindow = new MainWindow();

                autoLoginControlClient();

                if (controlCheckThread == null) {
                    controlCheckThread = new Thread(new CheckThread(), "ControlCheckThread");
                    controlCheckThread.setDaemon(true);
                    controlCheckThread.start();
                }
                
                // Initialize system tray
                initSystemTray();
                
                // Initialize sync managers (after UI)
                deleteManager = DeleteManager.getInstance();
                syncManager = SyncManager.getInstance();
                
                // Show window
                if (!startMinimized) {
                    mainWindow.setVisible(true);
                }
                
                logger.info("Application started successfully in {}ms", 
                    System.currentTimeMillis() - startTime);
                
                // Fire app started event
                fireAppEvent(new AppEvent(AppEvent.TYPE_APP_STARTED));
                
            } catch (Exception e) {
                logger.error("Failed to start application", e);
                showErrorDialog("启动失败", e.getMessage());
                System.exit(1);
            }
        });
    }

    private void autoLoginControlClient() {
        try {
            if (appConfig == null) {
                return;
            }
            if (!appConfig.isControlLoginRememberPassword()) {
                return;
            }
            String username = appConfig.getControlLoginUsername();
            String encrypted = appConfig.getControlLoginPassword();
            if (username == null || username.trim().isEmpty()) {
                return;
            }
            if (encrypted == null || encrypted.trim().isEmpty()) {
                return;
            }
            String password = DesUtil.decrypt(encrypted);
            if (password == null || password.isEmpty()) {
                return;
            }

            com.finalshell.control.ControlClient.getInstance().login(username.trim(), password, null);
        } catch (Exception e) {
            logger.debug("Auto login failed", e);
        }
    }
    
    /**
     * Check for existing process using UDP
     */
    private void checkExistingProcess() {
        try {
            DatagramSocket sendSocket = new DatagramSocket();
            sendSocket.setSoTimeout(300);
            
            // Start listener thread
            Thread listenerThread = new Thread(() -> {
                byte[] data = new byte[1024];
                DatagramPacket dp = new DatagramPacket(data, data.length);
                while (true) {
                    try {
                        sendSocket.receive(dp);
                        String command = new String(dp.getData(), 0, dp.getLength());
                        if ("fs_live".equals(command)) {
                            processExists = true;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            });
            listenerThread.start();
            
            // Send probe to all ports in range
            for (int i = 0; i < PORT_RANGE; i++) {
                try {
                    byte[] data = "fs_show".getBytes("UTF-8");
                    DatagramPacket dp = new DatagramPacket(data, data.length);
                    dp.setAddress(InetAddress.getByName("127.0.0.1"));
                    dp.setPort(BASE_PORT + i * 1000);
                    sendSocket.send(dp);
                } catch (Exception e) {
                    // Ignore
                }
            }
            
            listenerThread.join(500);
            sendSocket.close();
            
            if (processExists) {
                logger.info("Existing process detected, exiting");
                System.exit(0);
            }
            
            // Start monitor socket
            startMonitorSocket();
            
        } catch (Exception e) {
            logger.warn("Process detection failed", e);
        }
    }
    
    /**
     * Start UDP monitor socket for inter-process communication
     */
    private void startMonitorSocket() {
        for (int i = 0; i < PORT_RANGE; i++) {
            try {
                int port = BASE_PORT + i * 1000;
                monitorSocket = new DatagramSocket(port);
                logger.info("Monitor socket started on port {}", port);
                
                // Start listener thread
                Thread listenerThread = new Thread(() -> {
                    byte[] data = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(data, data.length);
                    while (!isShuttingDown) {
                        try {
                            monitorSocket.receive(dp);
                            String command = new String(dp.getData(), 0, dp.getLength());
                            if ("fs_show".equals(command)) {
                                // Bring window to front
                                if (mainWindow != null) {
                                    SwingUtilities.invokeLater(() -> {
                                        mainWindow.setVisible(true);
                                        mainWindow.toFront();
                                    });
                                }
                                // Send response
                                byte[] response = "fs_live".getBytes("UTF-8");
                                DatagramPacket responseDp = new DatagramPacket(response, response.length);
                                responseDp.setAddress(dp.getAddress());
                                responseDp.setPort(dp.getPort());
                                monitorSocket.send(responseDp);
                            }
                        } catch (Exception e) {
                            if (!isShuttingDown) {
                                logger.debug("Monitor socket error", e);
                            }
                        }
                    }
                }, "MonitorSocket");
                listenerThread.setDaemon(true);
                listenerThread.start();
                
                break;
            } catch (Exception e) {
                // Try next port
            }
        }
    }
    
    /**
     * Load system fonts
     */
    private void loadSystemFonts() {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        Locale enLocale = new Locale("en", "US");
        for (Font font : fonts) {
            systemFontNames.add(font.getFontName(enLocale));
        }
        logger.info("Loaded {} system fonts", systemFontNames.size());
    }
    
    /**
     * Load client configuration from JSON
     */
    private void loadClientConfig() {
        try {
            // Set default font based on OS
            String enFontName = selectDefaultEnFont();
            String cnFontName = selectDefaultCnFont();
            
            clientConfig.setTheme(appConfig.getTerminalTheme());
            
            logger.info("Client config loaded, fonts: {} / {}", enFontName, cnFontName);
        } catch (Exception e) {
            logger.error("Failed to load client config", e);
        }
    }
    
    /**
     * Select default English font
     */
    private String selectDefaultEnFont() {
        if (OSDetector.isWindows()) {
            if (systemFontNames.contains("DejaVu Sans Mono")) return "DejaVu Sans Mono";
            if (systemFontNames.contains("Consolas")) return "Consolas";
            if (systemFontNames.contains("Lucida Sans Typewriter Regular")) return "Lucida Sans Typewriter Regular";
        } else if (OSDetector.isLinux()) {
            if (systemFontNames.contains("DejaVu Sans Mono")) return "DejaVu Sans Mono";
        } else if (OSDetector.isMac()) {
            if (systemFontNames.contains("Monaco")) return "Monaco";
        }
        if (systemFontNames.contains("DejaVu Sans Mono")) return "DejaVu Sans Mono";
        return "Monospaced";
    }
    
    /**
     * Select default Chinese font
     */
    private String selectDefaultCnFont() {
        if (OSDetector.isWindows()) {
            if (systemFontNames.contains("Microsoft YaHei UI")) return "Microsoft YaHei UI";
            if (systemFontNames.contains("Microsoft YaHei")) return "Microsoft YaHei";
            if (systemFontNames.contains("NSimSun")) return "NSimSun";
            if (systemFontNames.contains("SimSun")) return "SimSun";
        } else if (OSDetector.isMac()) {
            if (systemFontNames.contains("PingFangSC")) return "PingFangSC";
            if (systemFontNames.contains("STSong")) return "STSong";
        }
        return "Monospaced";
    }
    
    /**
     * Initialize system tray
     */
    private void initSystemTray() {
        if (SystemTray.isSupported()) {
            try {
                systemTrayManager = new SystemTrayManager(mainWindow);
                logger.info("System tray initialized");
            } catch (Exception e) {
                logger.warn("Failed to initialize system tray", e);
            }
        }
    }
    
    private void initLookAndFeel() {
        try {
            // Try FlatLaf first (modern look)
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            logger.info("FlatLaf Dark Look and Feel applied");
        } catch (Exception e1) {
            try {
                // Fallback to WebLaF
                UIManager.setLookAndFeel("com.alee.laf.WebLookAndFeel");
                logger.info("WebLaF Look and Feel applied");
            } catch (Exception e2) {
                logger.warn("Custom L&F not available, using system L&F");
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e3) {
                    logger.error("Failed to set Look and Feel", e3);
                }
            }
        }
        
        // Apply font settings if available
        if (themeManager != null) {
            themeManager.applyTheme(clientConfig.getTheme());
        }
    }
    
    private static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    // Getters for managers
    
    public MainWindow getMainWindow() {
        return mainWindow;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public AppConfig getAppConfig() {
        return appConfig;
    }
    
    public ClientConfig getClientConfig() {
        return clientConfig;
    }
    
    public ThreadManager getThreadManager() {
        return threadManager;
    }
    
    public ThemeManager getThemeManager() {
        return themeManager;
    }
    
    public FontConfigManager getFontConfigManager() {
        return fontConfigManager;
    }
    
    public ImageManager getImageManager() {
        return imageManager;
    }
    
    public ProxyManager getProxyManager() {
        return proxyManager;
    }
    
    public SecretKeyManager getSecretKeyManager() {
        return secretKeyManager;
    }
    
    public HotkeyManager getHotkeyManager() {
        return hotkeyManager;
    }
    
    public TransTaskManager getTransTaskManager() {
        return transTaskManager;
    }
    
    public DeleteManager getDeleteManager() {
        return deleteManager;
    }
    
    public SyncManager getSyncManager() {
        return syncManager;
    }
    
    public LayoutConfigManager getLayoutConfigManager() {
        return layoutConfigManager;
    }
    
    public QuickCommandManager getQuickCommandManager() {
        return quickCommandManager;
    }
    
    public SystemTrayManager getSystemTrayManager() {
        return systemTrayManager;
    }
    
    /**
     * Save configuration
     */
    public synchronized void saveConfig() {
        try {
            if (configManager != null) {
                configManager.saveAll();
            }
            logger.debug("Configuration saved");
        } catch (Exception e) {
            logger.error("Failed to save configuration", e);
        }
    }
    
    /**
     * Exit the application
     */
    public synchronized void exit() {
        if (isShuttingDown) {
            return;
        }
        isShuttingDown = true;
        
        logger.info("Application exiting...");
        
        try {
            // Fire app closing event
            fireAppEvent(new AppEvent(AppEvent.TYPE_APP_CLOSING));
            
            // Save configuration
            saveConfig();
            
            // Close delete manager
            if (deleteManager != null) {
                deleteManager.shutdown();
            }
            
            // Clean up temp files
            cleanupTempFiles();
            
            // Close monitor socket
            if (monitorSocket != null) {
                monitorSocket.close();
            }
            
            // Shutdown thread manager
            if (threadManager != null) {
                threadManager.shutdown();
            }
            
            // Shutdown scheduled executor
            if (scheduledExecutor != null) {
                scheduledExecutor.shutdown();
            }
            
            // Dispose main window
            if (mainWindow != null) {
                mainWindow.dispose();
            }
            
            // Remove system tray icon
            if (systemTrayManager != null) {
                systemTrayManager.remove();
            }
            
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
        
        logger.info("Application shutdown complete");
        System.exit(0);
    }
    
    /**
     * Clean up temporary files
     */
    private void cleanupTempFiles() {
        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "finalshell");
            if (tempDir.exists()) {
                // Don't delete recursively, just clean old files
                File[] files = tempDir.listFiles();
                if (files != null) {
                    long cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000; // 24 hours
                    for (File file : files) {
                        if (file.lastModified() < cutoff) {
                            file.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Cleanup temp files error", e);
        }
    }
    
    // Event handling
    
    public void addAppListener(AppListener listener) {
        if (!appListeners.contains(listener)) {
            appListeners.add(listener);
        }
    }
    
    public void removeAppListener(AppListener listener) {
        appListeners.remove(listener);
    }
    
    private void fireAppEvent(AppEvent event) {
        for (AppListener listener : new ArrayList<>(appListeners)) {
            try {
                listener.onAppEvent(event);
            } catch (Exception e) {
                logger.error("Error firing app event", e);
            }
        }
    }
    
    // Utility methods
    
    public void setTheme(String themeName) {
        if (themeManager != null) {
            themeManager.applyTheme(themeName);
            clientConfig.setTheme(themeName);
            saveConfig();
        }
    }
    
    public void setFont(String enFontName, String cnFontName, int fontSize) {
        if (fontConfigManager != null) {
            fontConfigManager.setFont(enFontName, cnFontName, fontSize);
            saveConfig();
        }
    }
    
    public void showMainWindow() {
        if (mainWindow != null) {
            mainWindow.setVisible(true);
            mainWindow.toFront();
            mainWindow.requestFocus();
        }
    }
}
