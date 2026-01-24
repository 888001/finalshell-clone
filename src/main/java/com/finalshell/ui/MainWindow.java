package com.finalshell.ui;

import com.finalshell.app.App;
import com.finalshell.config.AppConfig;
import com.finalshell.config.ConfigManager;
import com.finalshell.sync.SyncDialog;
import com.finalshell.util.ResourceLoader;
import com.finalshell.layout.LayoutManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main Window - Application main frame
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MainWindow_DeepAnalysis.md, UI_Parameters_Reference.md
 */
public class MainWindow extends JFrame {
    
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);
    
    // Default dimensions from UI_Parameters_Reference.md
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final int DEFAULT_DIVIDER = 250;
    
    // UI Components
    private JSplitPane mainSplitPane;
    private JTabbedPane tabPane;
    private ConnectTreePanel connectTreePanel;
    private JToolBar toolBar;
    private JPanel statusBar;
    private JLabel statusLabel;
    private JMenuBar menuBar;
    
    private final ConfigManager configManager;
    private final AppConfig appConfig;
    
    public MainWindow() {
        this.configManager = ConfigManager.getInstance();
        this.appConfig = configManager.getAppConfig();
        
        initFrame();
        initMenuBar();
        initComponents();
        initLayout();
        initListeners();
        restoreWindowState();
        
        logger.info("MainWindow initialized");
    }
    
    private void initFrame() {
        setTitle("FinalShell Clone");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        
        // Set application icon
        Image logo = ResourceLoader.getInstance().getAppLogo();
        if (logo != null) {
            setIconImage(logo);
        }
    }
    
    private void initComponents() {
        // Toolbar
        toolBar = createToolBar();
        
        // Connection tree panel (left side)
        connectTreePanel = new ConnectTreePanel(this);
        
        // Tab pane for terminals/sftp (right side)
        tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Welcome tab
        JPanel welcomePanel = createWelcomePanel();
        tabPane.addTab("欢迎", welcomePanel);
        
        // Status bar
        statusBar = createStatusBar();
    }
    
    private JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorderPainted(false);
        
        // New connection button
        JButton newConnBtn = createToolButton("images/window_new.png", "新建连接", e -> showNewConnectionDialog());
        toolbar.add(newConnBtn);
        
        // New folder button
        JButton newFolderBtn = createToolButton("images/folder_new.png", "新建文件夹", e -> connectTreePanel.createNewFolder());
        toolbar.add(newFolderBtn);
        
        toolbar.addSeparator();
        
        // Collapse all button
        JButton collapseBtn = createToolButton("images/collapseall.png", "折叠全部", e -> connectTreePanel.collapseAll());
        toolbar.add(collapseBtn);
        
        // Expand all button
        JButton expandBtn = createToolButton("images/expandall.png", "展开全部", e -> connectTreePanel.expandAll());
        toolbar.add(expandBtn);
        
        toolbar.addSeparator();
        
        // Settings button
        JButton settingsBtn = createToolButton("images/config.png", "设置", e -> showSettingsDialog());
        toolbar.add(settingsBtn);
        
        return toolbar;
    }
    
    private JButton createToolButton(String iconPath, String tooltip, ActionListener action) {
        JButton button = new JButton();
        Icon icon = ResourceLoader.getInstance().getIcon(iconPath, 20, 20);
        if (icon != null) {
            button.setIcon(icon);
        } else {
            button.setText(tooltip.substring(0, 1));
        }
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        button.addActionListener(action);
        return button;
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        
        JLabel welcomeLabel = new JLabel("欢迎使用 FinalShell Clone", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel tipLabel = new JLabel("双击左侧连接列表开始使用", SwingConstants.CENTER);
        tipLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        tipLabel.setForeground(Color.GRAY);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        centerPanel.add(welcomeLabel, gbc);
        gbc.gridy = 1;
        centerPanel.add(tipLabel, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        
        statusLabel = new JLabel("就绪");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        JLabel versionLabel = new JLabel("v1.0.0");
        statusPanel.add(versionLabel, BorderLayout.EAST);
        
        return statusPanel;
    }
    
    private void initLayout() {
        setLayout(new BorderLayout());
        
        // Add toolbar at top
        add(toolBar, BorderLayout.NORTH);
        
        // Create split pane
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setLeftComponent(connectTreePanel);
        mainSplitPane.setRightComponent(tabPane);
        mainSplitPane.setDividerLocation(appConfig.getDividerLocation());
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setContinuousLayout(true);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Add status bar at bottom
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void initListeners() {
        // Window closing listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
        
        // Save window state on resize/move
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                saveWindowState();
            }
            
            @Override
            public void componentMoved(ComponentEvent e) {
                saveWindowState();
            }
        });
        
        // Save divider location
        mainSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> {
            appConfig.setDividerLocation(mainSplitPane.getDividerLocation());
        });
    }
    
    private void restoreWindowState() {
        int width = appConfig.getWindowWidth();
        int height = appConfig.getWindowHeight();
        int x = appConfig.getWindowX();
        int y = appConfig.getWindowY();
        
        if (width > 0 && height > 0) {
            setSize(width, height);
        } else {
            setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }
        
        if (x >= 0 && y >= 0) {
            setLocation(x, y);
        } else {
            setLocationRelativeTo(null);
        }
        
        if (appConfig.isWindowMaximized()) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }
    
    private void saveWindowState() {
        if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
            appConfig.setWindowWidth(getWidth());
            appConfig.setWindowHeight(getHeight());
            appConfig.setWindowX(getX());
            appConfig.setWindowY(getY());
            appConfig.setWindowMaximized(false);
        } else {
            appConfig.setWindowMaximized(true);
        }
    }
    
    private void handleWindowClosing() {
        if (appConfig.isConfirmOnClose()) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "确定要退出吗？",
                "确认退出",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        App.getInstance().exit();
    }
    
    public void showNewConnectionDialog() {
        ConnectionDialog dialog = new ConnectionDialog(this, null);
        dialog.setVisible(true);
    }
    
    public void showSettingsDialog() {
        SettingsDialog.show(this, ConfigManager.getInstance());
    }
    
    public void showSyncDialog() {
        SyncDialog dialog = new SyncDialog(this);
        dialog.setVisible(true);
    }
    
    private void initMenuBar() {
        menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newConnItem = new JMenuItem("新建连接", KeyEvent.VK_N);
        newConnItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newConnItem.addActionListener(e -> showNewConnectionDialog());
        fileMenu.add(newConnItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("退出", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> handleWindowClosing());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        
        // Tools menu
        JMenu toolsMenu = new JMenu("工具");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        
        JMenuItem syncItem = new JMenuItem("数据同步", KeyEvent.VK_S);
        syncItem.addActionListener(e -> showSyncDialog());
        toolsMenu.add(syncItem);
        
        toolsMenu.addSeparator();
        
        JMenuItem settingsItem = new JMenuItem("设置", KeyEvent.VK_P);
        settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.CTRL_DOWN_MASK));
        settingsItem.addActionListener(e -> showSettingsDialog());
        toolsMenu.add(settingsItem);
        
        menuBar.add(toolsMenu);
        
        // Help menu
        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem aboutItem = new JMenuItem("关于", KeyEvent.VK_A);
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "FinalShell Clone v1.0.0\n\n" +
            "基于 FinalShell 3.8.3 静态分析\n" +
            "全功能 SSH 终端管理工具\n\n" +
            "功能: SSH/SFTP/监控/端口转发/代理/RDP",
            "关于",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void setStatus(String message) {
        statusLabel.setText(message);
    }
    
    public JTabbedPane getTabPane() {
        return tabPane;
    }
    
    public void addTab(String title, Component component) {
        tabPane.addTab(title, component);
        tabPane.setSelectedComponent(component);
    }
    
    public void addTab(String title, Icon icon, Component component) {
        tabPane.addTab(title, icon, component);
        tabPane.setSelectedComponent(component);
    }
    
    public void removeTab(Component component) {
        tabPane.remove(component);
    }
    
    public void toggleFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (device.getFullScreenWindow() == this) {
            device.setFullScreenWindow(null);
        } else {
            device.setFullScreenWindow(this);
        }
    }
    
    public void openConnection(com.finalshell.config.ConnectConfig config) {
        if (config == null) return;
        // TODO: Implement connection opening based on config type
        setStatus("正在连接: " + config.getName());
    }
}
