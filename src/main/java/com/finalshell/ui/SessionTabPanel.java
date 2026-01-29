package com.finalshell.ui;

import com.finalshell.config.ConnectConfig;
import com.finalshell.forward.PortForwardPanel;
import com.finalshell.monitor.MonitorPanel;
import com.finalshell.rdp.RDPPanel;
import com.finalshell.sftp.SFTPPanel;
import com.finalshell.ssh.SSHSession;
import com.finalshell.terminal.TerminalPanel;
import com.finalshell.terminal.QuickCommandPanel;
import com.finalshell.terminal.ThemeManager;
import com.finalshell.terminal.TerminalTheme;
import com.finalshell.ui.dialog.FileSearchDialog;
import com.finalshell.util.Tools;
import com.finalshell.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Session Tab Panel - Combined Terminal + SFTP view
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MainWindow_DeepAnalysis.md
 */
public class SessionTabPanel extends JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionTabPanel.class);
    
    private final ConnectConfig config;
    private final MainWindow mainWindow;
    private final TerminalPanel terminalPanel;
    private SFTPPanel sftpPanel;
    private MonitorPanel monitorPanel;
    private PortForwardPanel forwardPanel;
    private QuickCommandPanel quickCmdPanel;
    private RDPPanel rdpPanel;
    
    private JSplitPane splitPane;
    private JSplitPane mainSplit;
    private JTabbedPane bottomTabs;
    private boolean sftpVisible = false;
    private boolean monitorVisible = false;
    
    public SessionTabPanel(ConnectConfig config, MainWindow mainWindow) {
        this.config = config;
        this.mainWindow = mainWindow;
        this.terminalPanel = new TerminalPanel(config);
        
        initLayout();
    }
    
    public SessionTabPanel(ConnectConfig config) {
        this(config, null);
    }
    
    private void initLayout() {
        setLayout(new BorderLayout());
        
        // Toolbar
        JToolBar toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Split pane for terminal/sftp
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(terminalPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(0);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        // SFTP toggle button
        JToggleButton sftpBtn = new JToggleButton("SFTP");
        sftpBtn.setToolTipText("显示/隐藏SFTP面板");
        sftpBtn.addActionListener(e -> toggleSFTP(sftpBtn.isSelected()));
        toolbar.add(sftpBtn);
        
        // Monitor toggle button
        JToggleButton monitorBtn = new JToggleButton("监控");
        monitorBtn.setToolTipText("显示/隐藏系统监控");
        monitorBtn.addActionListener(e -> toggleMonitor(monitorBtn.isSelected()));
        toolbar.add(monitorBtn);
        
        // Port forward toggle button
        JToggleButton forwardBtn = new JToggleButton("转发");
        forwardBtn.setToolTipText("显示/隐藏端口转发");
        forwardBtn.addActionListener(e -> toggleForward(forwardBtn.isSelected()));
        toolbar.add(forwardBtn);
        
        // Quick command toggle button
        JToggleButton quickCmdBtn = new JToggleButton("快捷");
        quickCmdBtn.setToolTipText("显示/隐藏快捷命令");
        quickCmdBtn.addActionListener(e -> toggleQuickCommand(quickCmdBtn.isSelected()));
        toolbar.add(quickCmdBtn);
        
        // RDP toggle button
        JButton rdpBtn = new JButton("RDP");
        rdpBtn.setToolTipText("远程桌面连接");
        rdpBtn.addActionListener(e -> showRDPDialog());
        toolbar.add(rdpBtn);

        JButton searchBtn = new JButton("搜索");
        searchBtn.setToolTipText("远程文件搜索");
        searchBtn.addActionListener(e -> {
            SSHSession sshSession = terminalPanel.getSSHSession();
            if (sshSession == null || !sshSession.isConnected()) {
                JOptionPane.showMessageDialog(SessionTabPanel.this,
                    "请先连接SSH后再搜索文件", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            java.awt.Window w = SwingUtilities.getWindowAncestor(SessionTabPanel.this);
            java.awt.Frame owner = (w instanceof java.awt.Frame) ? (java.awt.Frame) w : null;
            FileSearchDialog dialog = new FileSearchDialog(owner, sshSession);
            dialog.setSearchPath("/");
            dialog.setCallback(filePath -> {
                if (filePath == null) return;
                Tools.copyToClipboard(filePath);
                JOptionPane.showMessageDialog(SessionTabPanel.this,
                    "已复制路径到剪贴板:\n" + filePath, "提示", JOptionPane.INFORMATION_MESSAGE);
            });
            dialog.setVisible(true);
        });
        toolbar.add(searchBtn);
        
        toolbar.addSeparator();
        
        // Theme selector
        JComboBox<String> themeCombo = new JComboBox<>();
        ThemeManager themeManager = ThemeManager.getInstance();
        for (String themeName : themeManager.getAvailableThemes()) {
            themeCombo.addItem(themeName);
        }
        themeCombo.setSelectedItem(themeManager.getCurrentTheme().getName());
        themeCombo.setToolTipText("终端主题");
        themeCombo.addActionListener(e -> {
            String selected = (String) themeCombo.getSelectedItem();
            if (selected != null) {
                themeManager.setCurrentTheme(selected);
            }
        });
        toolbar.add(new JLabel("主题:"));
        toolbar.add(themeCombo);
        
        toolbar.addSeparator();
        
        // Reconnect button
        JButton reconnectBtn = new JButton("重连");
        reconnectBtn.addActionListener(e -> terminalPanel.reconnect());
        toolbar.add(reconnectBtn);
        
        // Disconnect button
        JButton disconnectBtn = new JButton("断开");
        disconnectBtn.addActionListener(e -> terminalPanel.disconnect());
        toolbar.add(disconnectBtn);
        
        toolbar.addSeparator();
        
        // Session info
        JLabel infoLabel = new JLabel(config.getUserName() + "@" + config.getHost() + ":" + config.getPort());
        infoLabel.setForeground(Color.GRAY);
        toolbar.add(infoLabel);
        
        return toolbar;
    }
    
    /**
     * Toggle SFTP panel visibility
     */
    private void toggleSFTP(boolean show) {
        SSHSession sshSession = terminalPanel.getSSHSession();
        if (show) {
            if (sshSession == null || !sshSession.isConnected()) {
                JOptionPane.showMessageDialog(this, 
                    "请先连接SSH后再打开SFTP", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (sftpPanel == null) {
                sftpPanel = new SFTPPanel(sshSession);
            }
            showBottomPanel("SFTP", sftpPanel);
            sftpVisible = true;
        } else {
            hideBottomPanel();
            sftpVisible = false;
        }
    }
    
    /**
     * Toggle Monitor panel visibility
     */
    private void toggleMonitor(boolean show) {
        SSHSession sshSession = terminalPanel.getSSHSession();
        if (show) {
            if (sshSession == null || !sshSession.isConnected()) {
                JOptionPane.showMessageDialog(this, 
                    "请先连接SSH后再打开监控", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (monitorPanel == null) {
                monitorPanel = new MonitorPanel(sshSession);
            }
            showBottomPanel("监控", monitorPanel);
            monitorVisible = true;
        } else {
            if (monitorPanel != null) {
                monitorPanel.stopMonitoring();
            }
            hideBottomPanel();
            monitorVisible = false;
        }
    }
    
    /**
     * Toggle Port Forward panel visibility
     */
    private void toggleForward(boolean show) {
        SSHSession sshSession = terminalPanel.getSSHSession();
        if (show) {
            if (sshSession == null || !sshSession.isConnected()) {
                JOptionPane.showMessageDialog(this, 
                    "请先连接SSH后再打开端口转发", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (forwardPanel == null) {
                forwardPanel = new PortForwardPanel(sshSession);
            }
            showBottomPanel("端口转发", forwardPanel);
        } else {
            hideBottomPanel();
        }
    }
    
    private void showBottomPanel(String title, JPanel panel) {
        splitPane.setBottomComponent(panel);
        splitPane.setDividerSize(5);
        splitPane.setDividerLocation(0.6);
        revalidate();
        repaint();
    }
    
    private void hideBottomPanel() {
        splitPane.setBottomComponent(null);
        splitPane.setDividerSize(0);
        revalidate();
        repaint();
    }
    
    /**
     * Toggle Quick Command panel visibility
     */
    private void toggleQuickCommand(boolean show) {
        if (show) {
            if (quickCmdPanel == null) {
                quickCmdPanel = new QuickCommandPanel((cmd, sendEnter) -> {
                    try {
                        // Send command through JediTerm's TtyConnector
                        com.jediterm.terminal.ui.JediTermWidget widget = terminalPanel.getTerminalWidget();
                        if (widget != null && widget.getTtyConnector() != null) {
                            String command = sendEnter ? cmd + "\n" : cmd;
                            widget.getTtyConnector().write(command.getBytes(
                                java.nio.charset.Charset.forName(terminalPanel.getConfig().getCharset())));
                        }
                    } catch (Exception ex) {
                        logger.error("Failed to send command", ex);
                    }
                });
            }
            showRightPanel(quickCmdPanel);
        } else {
            hideRightPanel();
        }
    }
    
    private void showRightPanel(JPanel panel) {
        if (mainSplit == null) {
            mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            remove(splitPane);
            mainSplit.setLeftComponent(splitPane);
            mainSplit.setResizeWeight(0.8);
            add(mainSplit, BorderLayout.CENTER);
        }
        mainSplit.setRightComponent(panel);
        mainSplit.setDividerLocation(0.8);
        revalidate();
        repaint();
    }
    
    private void hideRightPanel() {
        if (mainSplit != null) {
            mainSplit.setRightComponent(null);
        }
        revalidate();
        repaint();
    }
    
    /**
     * Show RDP connection dialog
     */
    private void showRDPDialog() {
        SSHSession sshSession = terminalPanel.getSSHSession();
        if (sshSession == null || !sshSession.isConnected()) {
            JOptionPane.showMessageDialog(this, 
                "请先连接SSH后再使用RDP功能", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (rdpPanel == null) {
            rdpPanel = new RDPPanel(sshSession);
        }
        showBottomPanel("RDP远程桌面", rdpPanel);
    }
    
    /**
     * Connect to SSH
     */
    public void connect() {
        terminalPanel.connect();
    }
    
    /**
     * Disconnect
     */
    public void disconnect() {
        terminalPanel.disconnect();
        if (sftpPanel != null) {
            sftpPanel.close();
        }
        if (monitorPanel != null) {
            monitorPanel.close();
        }
        if (forwardPanel != null) {
            forwardPanel.close();
        }
    }
    
    public TerminalPanel getTerminalPanel() {
        return terminalPanel;
    }
    
    public SFTPPanel getSFTPPanel() {
        return sftpPanel;
    }
    
    public ConnectConfig getConfig() {
        return config;
    }
    
    public boolean isConnected() {
        return terminalPanel.isConnected();
    }
    
    /**
     * Reconnect session
     */
    public void reconnect() {
        disconnect();
        connect();
    }
    
    /**
     * Close session and release resources
     */
    public void close() {
        disconnect();
        terminalPanel.close();
    }
    
    /**
     * Update terminal font size
     */
    public void updateFontSize(int size) {
        terminalPanel.setFontSize(size);
    }
    
    /**
     * Get main window reference
     */
    public MainWindow getMainWindow() {
        return mainWindow;
    }
}
