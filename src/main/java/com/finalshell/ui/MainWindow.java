package com.finalshell.ui;

import com.finalshell.app.App;
import com.finalshell.app.AppEvent;
import com.finalshell.app.AppListener;
import com.finalshell.config.AppConfig;
import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.control.ControlClient;
import com.finalshell.control.LoginDialog;
import com.finalshell.control.SetProListener;
import com.finalshell.key.KeyManagerDialog;
import com.finalshell.rdp.RDPConfig;
import com.finalshell.rdp.RDPPanel;
import com.finalshell.sync.SyncDialog;
import com.finalshell.ui.dialog.ProIntroDialog;
import com.finalshell.update.UpdateChecker;
import com.finalshell.util.ResourceLoader;
import com.finalshell.layout.LayoutManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Window - Application main frame
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MainWindow_DeepAnalysis.md, UI_Parameters_Reference.md
 */
public class MainWindow extends JFrame implements AppListener {
    
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
    private JLabel proLabel;
    private JMenuBar menuBar;
    
    // Menu items for state toggle
    private JCheckBoxMenuItem showSidebarItem;
    private JCheckBoxMenuItem showStatusBarItem;
    private JCheckBoxMenuItem showToolBarItem;
    
    // Session management
    private List<SessionTabPanel> sessionPanels = new ArrayList<>();
    private SessionTabPanel currentSession;
    
    private final ConfigManager configManager;
    private final AppConfig appConfig;
    private boolean sidebarVisible = true;
    private boolean statusBarVisible = true;
    private int lastDividerLocation = DEFAULT_DIVIDER;
    
    public MainWindow() {
        this.configManager = ConfigManager.getInstance();
        this.appConfig = configManager.getAppConfig();
        
        initFrame();
        initMenuBar();
        initComponents();
        initProStatusListener();
        initLayout();
        initListeners();
        initKeyBindings();
        restoreWindowState();
        
        // Register as app listener
        if (App.getInstance() != null) {
            App.getInstance().addAppListener(this);
        }
        
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
        
        toolbar.addSeparator();
        
        // Key manager button
        JButton keyBtn = createToolButton("images/key.png", "密钥管理", e -> showKeyManagerDialog());
        toolbar.add(keyBtn);
        
        // Sync button
        JButton syncBtn = createToolButton("images/sync.png", "数据同步", e -> showSyncDialog());
        toolbar.add(syncBtn);
        
        toolbar.add(Box.createHorizontalGlue());
        
        // Full screen button
        JButton fullScreenBtn = createToolButton("images/fullscreen.png", "全屏 (F11)", e -> toggleFullScreen());
        toolbar.add(fullScreenBtn);
        
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

        proLabel = new JLabel(" ");
        proLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(proLabel, BorderLayout.CENTER);
        
        JLabel versionLabel = new JLabel("v1.0.0");
        statusPanel.add(versionLabel, BorderLayout.EAST);
        
        return statusPanel;
    }

    private void initProStatusListener() {
        ControlClient.getInstance().setSetProListener(new SetProListener() {
            private volatile boolean pro;
            private volatile boolean valid;

            @Override
            public void setProStatus(boolean isPro, boolean isValid) {
                this.pro = isPro;
                this.valid = isValid;
                SwingUtilities.invokeLater(() -> {
                    if (proLabel != null) {
                        if (!pro) {
                            proLabel.setText("Free");
                        } else if (valid) {
                            proLabel.setText("Pro");
                        } else {
                            proLabel.setText("Pro(无效)");
                        }
                    }
                });
            }

            @Override
            public boolean isPro() {
                return pro;
            }

            @Override
            public boolean isProValid() {
                return valid;
            }
        });
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

    public void showLoginDialog() {
        LoginDialog dialog = new LoginDialog(this);
        dialog.setCallback((username, isPro) -> ControlClient.getInstance().checkLicense(null));
        dialog.setVisible(true);
    }

    public void showProIntroDialog() {
        ProIntroDialog dialog = new ProIntroDialog(this);
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
        
        JMenuItem newFolderItem = new JMenuItem("新建文件夹");
        newFolderItem.addActionListener(e -> connectTreePanel.createNewFolder());
        fileMenu.add(newFolderItem);
        
        fileMenu.addSeparator();
        
        JMenuItem importItem = new JMenuItem("导入连接...");
        importItem.addActionListener(e -> importConnections());
        fileMenu.add(importItem);
        
        JMenuItem exportItem = new JMenuItem("导出连接...");
        exportItem.addActionListener(e -> exportConnections());
        fileMenu.add(exportItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("退出", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> handleWindowClosing());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        
        // View menu
        JMenu viewMenu = new JMenu("视图");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        showSidebarItem = new JCheckBoxMenuItem("显示侧边栏", true);
        showSidebarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        showSidebarItem.addActionListener(e -> toggleSidebar());
        viewMenu.add(showSidebarItem);
        
        showToolBarItem = new JCheckBoxMenuItem("显示工具栏", true);
        showToolBarItem.addActionListener(e -> toggleToolBar());
        viewMenu.add(showToolBarItem);
        
        showStatusBarItem = new JCheckBoxMenuItem("显示状态栏", true);
        showStatusBarItem.addActionListener(e -> toggleStatusBar());
        viewMenu.add(showStatusBarItem);
        
        viewMenu.addSeparator();
        
        JMenuItem fullScreenItem = new JMenuItem("全屏模式");
        fullScreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
        fullScreenItem.addActionListener(e -> toggleFullScreen());
        viewMenu.add(fullScreenItem);
        
        viewMenu.addSeparator();
        
        JMenuItem zoomInItem = new JMenuItem("放大字体");
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
        zoomInItem.addActionListener(e -> zoomFont(1));
        viewMenu.add(zoomInItem);
        
        JMenuItem zoomOutItem = new JMenuItem("缩小字体");
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        zoomOutItem.addActionListener(e -> zoomFont(-1));
        viewMenu.add(zoomOutItem);
        
        JMenuItem zoomResetItem = new JMenuItem("重置字体大小");
        zoomResetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK));
        zoomResetItem.addActionListener(e -> resetFontSize());
        viewMenu.add(zoomResetItem);
        
        menuBar.add(viewMenu);
        
        // Session menu
        JMenu sessionMenu = new JMenu("会话");
        sessionMenu.setMnemonic(KeyEvent.VK_S);
        
        JMenuItem reconnectItem = new JMenuItem("重新连接");
        reconnectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        reconnectItem.addActionListener(e -> reconnectCurrentSession());
        sessionMenu.add(reconnectItem);
        
        JMenuItem disconnectItem = new JMenuItem("断开连接");
        disconnectItem.addActionListener(e -> disconnectCurrentSession());
        sessionMenu.add(disconnectItem);
        
        sessionMenu.addSeparator();
        
        JMenuItem closeTabItem = new JMenuItem("关闭当前标签页");
        closeTabItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
        closeTabItem.addActionListener(e -> closeCurrentTab());
        sessionMenu.add(closeTabItem);
        
        JMenuItem closeAllItem = new JMenuItem("关闭所有标签页");
        closeAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        closeAllItem.addActionListener(e -> closeAllTabs());
        sessionMenu.add(closeAllItem);
        
        sessionMenu.addSeparator();
        
        JMenuItem prevTabItem = new JMenuItem("上一个标签页");
        prevTabItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.CTRL_DOWN_MASK));
        prevTabItem.addActionListener(e -> switchToPreviousTab());
        sessionMenu.add(prevTabItem);
        
        JMenuItem nextTabItem = new JMenuItem("下一个标签页");
        nextTabItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_DOWN_MASK));
        nextTabItem.addActionListener(e -> switchToNextTab());
        sessionMenu.add(nextTabItem);
        
        menuBar.add(sessionMenu);
        
        // Tools menu
        JMenu toolsMenu = new JMenu("工具");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        
        JMenuItem syncItem = new JMenuItem("数据同步", KeyEvent.VK_S);
        syncItem.addActionListener(e -> showSyncDialog());
        toolsMenu.add(syncItem);
        
        JMenuItem keyManagerItem = new JMenuItem("密钥管理");
        keyManagerItem.addActionListener(e -> showKeyManagerDialog());
        toolsMenu.add(keyManagerItem);

        toolsMenu.addSeparator();

        JMenuItem loginItem = new JMenuItem("账号登录...");
        loginItem.addActionListener(e -> showLoginDialog());
        toolsMenu.add(loginItem);

        JMenuItem proItem = new JMenuItem("Pro/升级...");
        proItem.addActionListener(e -> showProIntroDialog());
        toolsMenu.add(proItem);
        
        toolsMenu.addSeparator();
        
        JMenuItem settingsItem = new JMenuItem("设置", KeyEvent.VK_P);
        settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.CTRL_DOWN_MASK));
        settingsItem.addActionListener(e -> showSettingsDialog());
        toolsMenu.add(settingsItem);
        
        menuBar.add(toolsMenu);
        
        // Help menu
        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem checkUpdateItem = new JMenuItem("检查更新...");
        checkUpdateItem.addActionListener(e -> checkForUpdates());
        helpMenu.add(checkUpdateItem);
        
        helpMenu.addSeparator();
        
        JMenuItem aboutItem = new JMenuItem("关于", KeyEvent.VK_A);
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void initKeyBindings() {
        // F11 for fullscreen
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullScreen");
        getRootPane().getActionMap().put("toggleFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen();
            }
        });
        
        // Tab switching with Alt+1-9
        for (int i = 1; i <= 9; i++) {
            final int tabIndex = i - 1;
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, InputEvent.ALT_DOWN_MASK), "switchToTab" + i);
            getRootPane().getActionMap().put("switchToTab" + i, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (tabIndex < tabPane.getTabCount()) {
                        tabPane.setSelectedIndex(tabIndex);
                    }
                }
            });
        }
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
    
    public void openConnection(ConnectConfig config) {
        if (config == null) return;
        
        setStatus("正在连接: " + config.getName());

        if (config.getType() == ConnectConfig.TYPE_RDP) {
            RDPConfig rdpConfig = new RDPConfig();
            rdpConfig.setUseSshTunnel(false);
            rdpConfig.setName(config.getName());
            rdpConfig.setHost(config.getHost());
            rdpConfig.setPort(config.getPort());
            rdpConfig.setUsername(config.getUserName());
            rdpConfig.setPassword(config.getPassword());
            rdpConfig.setWidth(config.getRdpWidth());
            rdpConfig.setHeight(config.getRdpHeight());
            rdpConfig.setFullscreen(config.isRdpFullscreen());

            RDPPanel rdpPanel = new RDPPanel(rdpConfig, null);

            String title = config.getName();
            int index = tabPane.getTabCount();
            tabPane.addTab(title, rdpPanel);
            tabPane.setTabComponentAt(index, createTabComponent(title, rdpPanel));
            tabPane.setSelectedIndex(index);

            rdpPanel.connect();
            logger.info("Opened RDP connection: {}", config.getName());
            return;
        }
        
        // Create session tab panel
        SessionTabPanel sessionPanel = new SessionTabPanel(config, this);
        sessionPanels.add(sessionPanel);
        
        // Add tab with close button
        String title = config.getName();
        int index = tabPane.getTabCount();
        tabPane.addTab(title, sessionPanel);
        tabPane.setTabComponentAt(index, createTabComponent(title, sessionPanel));
        tabPane.setSelectedIndex(index);
        
        // Connect
        sessionPanel.connect();
        
        logger.info("Opened connection: {}", config.getName());
    }
    
    private JPanel createTabComponent(String title, Component tabContent) {
        JPanel tabComponent = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabComponent.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        tabComponent.add(titleLabel);
        
        JButton closeButton = new JButton("×");
        closeButton.setMargin(new Insets(0, 2, 0, 2));
        closeButton.setFont(closeButton.getFont().deriveFont(10f));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusable(false);
        closeButton.addActionListener(e -> {
            int index = tabPane.indexOfComponent(tabContent);
            if (index >= 0) {
                closeTab(index);
            }
        });
        tabComponent.add(closeButton);
        
        return tabComponent;
    }
    
    // View toggle methods
    
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        if (sidebarVisible) {
            mainSplitPane.setDividerLocation(lastDividerLocation);
            connectTreePanel.setVisible(true);
        } else {
            lastDividerLocation = mainSplitPane.getDividerLocation();
            mainSplitPane.setDividerLocation(0);
            connectTreePanel.setVisible(false);
        }
        showSidebarItem.setSelected(sidebarVisible);
    }
    
    private void toggleToolBar() {
        toolBar.setVisible(!toolBar.isVisible());
        showToolBarItem.setSelected(toolBar.isVisible());
    }
    
    private void toggleStatusBar() {
        statusBarVisible = !statusBarVisible;
        statusBar.setVisible(statusBarVisible);
        showStatusBarItem.setSelected(statusBarVisible);
    }
    
    // Session management methods
    
    private void reconnectCurrentSession() {
        Component selected = tabPane.getSelectedComponent();
        if (selected instanceof SessionTabPanel) {
            ((SessionTabPanel) selected).reconnect();
        }
    }
    
    private void disconnectCurrentSession() {
        Component selected = tabPane.getSelectedComponent();
        if (selected instanceof SessionTabPanel) {
            ((SessionTabPanel) selected).disconnect();
        }
    }
    
    private void closeCurrentTab() {
        int index = tabPane.getSelectedIndex();
        if (index >= 0) {
            closeTab(index);
        }
    }
    
    private void closeTab(int index) {
        Component component = tabPane.getComponentAt(index);
        if (component instanceof SessionTabPanel) {
            SessionTabPanel session = (SessionTabPanel) component;
            session.close();
            sessionPanels.remove(session);
        } else if (component instanceof RDPPanel) {
            ((RDPPanel) component).close();
        }
        tabPane.removeTabAt(index);
        logger.info("Tab closed: {}", index);
    }
    
    private void closeAllTabs() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要关闭所有标签页吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        while (tabPane.getTabCount() > 0) {
            closeTab(0);
        }
    }
    
    private void switchToPreviousTab() {
        int current = tabPane.getSelectedIndex();
        if (current > 0) {
            tabPane.setSelectedIndex(current - 1);
        } else if (tabPane.getTabCount() > 0) {
            tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
        }
    }
    
    private void switchToNextTab() {
        int current = tabPane.getSelectedIndex();
        if (current < tabPane.getTabCount() - 1) {
            tabPane.setSelectedIndex(current + 1);
        } else if (tabPane.getTabCount() > 0) {
            tabPane.setSelectedIndex(0);
        }
    }
    
    // Font zoom methods
    
    private void zoomFont(int delta) {
        int currentSize = appConfig.getTerminalFontSize();
        int newSize = Math.max(8, Math.min(72, currentSize + delta));
        appConfig.setTerminalFontSize(newSize);
        
        // Apply to all sessions
        for (SessionTabPanel session : sessionPanels) {
            session.updateFontSize(newSize);
        }
        
        setStatus("字体大小: " + newSize);
    }
    
    private void resetFontSize() {
        appConfig.setTerminalFontSize(14);
        for (SessionTabPanel session : sessionPanels) {
            session.updateFontSize(14);
        }
        setStatus("字体大小已重置");
    }
    
    // Dialog methods
    
    public void showKeyManagerDialog() {
        KeyManagerDialog dialog = new KeyManagerDialog(this);
        dialog.setVisible(true);
    }
    
    private void checkForUpdates() {
        setStatus("正在检查更新...");
        UpdateChecker.getInstance().checkUpdateAsync(new UpdateChecker.UpdateCallback() {
            @Override
            public void onUpdateAvailable(UpdateChecker.UpdateInfo info) {
                setStatus("发现新版本: " + info.getVersionName());
                UpdateChecker.getInstance().showUpdateDialog(MainWindow.this, info);
                setStatus("就绪");
            }

            @Override
            public void onNoUpdate() {
                JOptionPane.showMessageDialog(MainWindow.this, "当前已是最新版本", "检查更新", JOptionPane.INFORMATION_MESSAGE);
                setStatus("就绪");
            }

            @Override
            public void onError(String message) {
                JOptionPane.showMessageDialog(MainWindow.this, "检查更新失败: " + message, "检查更新", JOptionPane.ERROR_MESSAGE);
                setStatus("就绪");
            }
        });
    }
    
    private void importConnections() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导入连接");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON 文件", "json"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = chooser.getSelectedFile();
                configManager.importConnections(file);
                setStatus("导入完成: " + file.getName());
                connectTreePanel.refreshTree();
            } catch (Exception e) {
                logger.error("导入失败", e);
                JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportConnections() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出连接");
        chooser.setSelectedFile(new java.io.File("connections.json"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".json")) {
                    file = new java.io.File(file.getAbsolutePath() + ".json");
                }
                configManager.exportConnections(file);
                setStatus("导出完成: " + file.getName());
            } catch (Exception e) {
                logger.error("导出失败", e);
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // AppListener implementation
    
    @Override
    public void onAppEvent(AppEvent event) {
        switch (event.getType()) {
            case AppEvent.TYPE_THEME_CHANGED:
                SwingUtilities.updateComponentTreeUI(this);
                break;
            case AppEvent.TYPE_FONT_CHANGED:
                // Update font for all sessions
                int fontSize = appConfig.getTerminalFontSize();
                for (SessionTabPanel session : sessionPanels) {
                    session.updateFontSize(fontSize);
                }
                break;
        }
    }
    
    // Getter methods
    
    public List<SessionTabPanel> getSessionPanels() {
        return sessionPanels;
    }
    
    public SessionTabPanel getCurrentSession() {
        Component selected = tabPane.getSelectedComponent();
        if (selected instanceof SessionTabPanel) {
            return (SessionTabPanel) selected;
        }
        return null;
    }
}
