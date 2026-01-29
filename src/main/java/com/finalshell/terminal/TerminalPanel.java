package com.finalshell.terminal;

import com.finalshell.config.ConnectConfig;
import com.finalshell.ssh.SSHException;
import com.finalshell.ssh.SSHSession;
import com.finalshell.util.ResourceLoader;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Terminal Panel - JediTerm based terminal emulator
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Terminal_DeepAnalysis.md, UI_Parameters_Reference.md
 */
public class TerminalPanel extends JPanel implements SSHSession.SSHSessionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TerminalPanel.class);
    
    private final ConnectConfig config;
    private SSHSession sshSession;
    private JediTermWidget terminalWidget;
    private SSHTtyConnector ttyConnector;
    
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JLabel charsetLabel;
    
    private boolean connected = false;
    
    public TerminalPanel(ConnectConfig config) {
        this.config = config;
        initComponents();
        initLayout();
    }
    
    private void initComponents() {
        // Status panel
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        statusLabel = new JLabel("未连接");
        statusLabel.setForeground(Color.GRAY);
        
        charsetLabel = new JLabel(config.getCharset());
        charsetLabel.setForeground(Color.GRAY);
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(charsetLabel, BorderLayout.EAST);
    }
    
    private void initLayout() {
        setLayout(new BorderLayout());
        add(statusPanel, BorderLayout.SOUTH);
        
        // Placeholder before connection
        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(new Color(30, 30, 30));
        JLabel label = new JLabel("正在连接 " + config.getHost() + "...");
        label.setForeground(Color.WHITE);
        placeholder.add(label);
        add(placeholder, BorderLayout.CENTER);
    }
    
    /**
     * Connect to SSH server and start terminal
     */
    public void connect() {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("正在连接...");
                
                // Create SSH session
                sshSession = new SSHSession(config);
                sshSession.addListener(TerminalPanel.this);
                
                // Connect
                sshSession.connect();
                
                // Open shell
                sshSession.openShell();
                
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                if (!chunks.isEmpty()) {
                    statusLabel.setText(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    setupTerminal();
                    connected = true;
                    statusLabel.setText("已连接");
                    statusLabel.setForeground(new Color(0, 180, 0));
                } catch (Exception e) {
                    logger.error("Connection failed", e);
                    statusLabel.setText("连接失败: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    showError("连接失败", e.getMessage());
                }
            }
        }.execute();
    }
    
    /**
     * Setup JediTerm terminal widget
     */
    private void setupTerminal() {
        // Remove placeholder
        removeAll();
        
        // Create TTY connector
        ttyConnector = new SSHTtyConnector(sshSession, Charset.forName(config.getCharset()));
        
        // Create terminal settings
        TerminalSettingsProvider settingsProvider = new TerminalSettingsProvider(config);
        
        // Create terminal widget
        terminalWidget = new JediTermWidget(settingsProvider);
        terminalWidget.setTtyConnector(ttyConnector);
        terminalWidget.start();
        
        // Add resize listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (sshSession != null && sshSession.isShellOpen()) {
                    com.jediterm.core.util.TermSize termSize = terminalWidget.getTerminalPanel().getTerminalSizeFromComponent();
                    if (termSize != null) {
                        sshSession.resizeTerminal(termSize.getColumns(), termSize.getRows());
                    }
                }
            }
        });
        
        // Layout
        setLayout(new BorderLayout());
        add(terminalWidget, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
        
        // Focus terminal
        SwingUtilities.invokeLater(() -> terminalWidget.requestFocusInWindow());
        
        logger.info("Terminal setup complete for {}", config.getName());
    }
    
    /**
     * Disconnect from SSH server
     */
    public void disconnect() {
        if (ttyConnector != null) {
            ttyConnector.close();
        }
        
        if (sshSession != null) {
            sshSession.disconnect();
        }
        
        connected = false;
        statusLabel.setText("已断开");
        statusLabel.setForeground(Color.GRAY);
    }
    
    /**
     * Reconnect to SSH server
     */
    public void reconnect() {
        disconnect();
        connect();
    }
    
    /**
     * Send text to terminal
     */
    public void sendText(String text) {
        if (ttyConnector != null) {
            try {
                ttyConnector.write(text);
            } catch (IOException e) {
                logger.error("Failed to send text", e);
            }
        }
    }
    
    /**
     * Send command with newline
     */
    public void sendCommand(String command) {
        sendText(command + "\n");
    }
    
    @Override
    public void onSSHEvent(SSHSession session, SSHSession.SSHEvent event, String message) {
        SwingUtilities.invokeLater(() -> {
            switch (event) {
                case CONNECTING:
                    statusLabel.setText("正在连接...");
                    statusLabel.setForeground(Color.ORANGE);
                    break;
                case CONNECTED:
                    statusLabel.setText("SSH已连接");
                    statusLabel.setForeground(new Color(0, 180, 0));
                    break;
                case SHELL_OPENED:
                    statusLabel.setText("已连接");
                    statusLabel.setForeground(new Color(0, 180, 0));
                    break;
                case DISCONNECTED:
                    statusLabel.setText("已断开");
                    statusLabel.setForeground(Color.GRAY);
                    connected = false;
                    break;
                case ERROR:
                    statusLabel.setText("错误: " + message);
                    statusLabel.setForeground(Color.RED);
                    break;
            }
        });
    }
    
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    // Getters
    public boolean isConnected() {
        return connected && sshSession != null && sshSession.isConnected();
    }
    
    public SSHSession getSSHSession() {
        return sshSession;
    }
    
    public ConnectConfig getConfig() {
        return config;
    }
    
    public JediTermWidget getTerminalWidget() {
        return terminalWidget;
    }
    
    /**
     * Close terminal and release resources
     */
    public void close() {
        disconnect();
        if (terminalWidget != null) {
            terminalWidget.close();
        }
    }
    
    /**
     * Set terminal font size
     */
    public void setFontSize(int size) {
        if (terminalWidget != null) {
            // JediTerm uses TerminalSettings to control font
            Font currentFont = terminalWidget.getTerminalPanel().getFont();
            if (currentFont != null) {
                Font newFont = currentFont.deriveFont((float) size);
                terminalWidget.getTerminalPanel().setFont(newFont);
                terminalWidget.getTerminalPanel().repaint();
            }
        }
    }
}
