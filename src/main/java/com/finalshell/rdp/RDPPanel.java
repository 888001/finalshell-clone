package com.finalshell.rdp;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.ssh.SSHSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * RDP Panel - Remote Desktop connection panel
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class RDPPanel extends JPanel implements RDPSession.RDPListener {
    
    private static final Logger logger = LoggerFactory.getLogger(RDPPanel.class);
    
    private final RDPConfig rdpConfig;
    private final ConnectConfig sshConfig;
    
    private SSHSession sshSession;
    private RDPSession rdpSession;
    
    private JLabel statusLabel;
    private JButton connectBtn;
    private JButton disconnectBtn;
    private JProgressBar progressBar;
    
    public RDPPanel(RDPConfig rdpConfig, ConnectConfig sshConfig) {
        this.rdpConfig = rdpConfig;
        this.sshConfig = sshConfig;
        
        initComponents();
        initLayout();
    }
    
    private void initComponents() {
        statusLabel = new JLabel("未连接");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 14f));
        
        connectBtn = new JButton("连接RDP");
        connectBtn.addActionListener(e -> connect());
        
        disconnectBtn = new JButton("断开");
        disconnectBtn.setEnabled(false);
        disconnectBtn.addActionListener(e -> disconnect());
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
    }
    
    private void initLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("RDP连接信息"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // RDP target
        gbc.gridx = 0; gbc.gridy = row;
        infoPanel.add(new JLabel("目标主机:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(rdpConfig.getRdpHost() + ":" + rdpConfig.getRdpPort()), gbc);
        
        // SSH tunnel
        if (rdpConfig.isUseSshTunnel() && sshConfig != null) {
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            infoPanel.add(new JLabel("SSH隧道:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(sshConfig.getUserName() + "@" + sshConfig.getHost()), gbc);
            
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            infoPanel.add(new JLabel("本地端口:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(String.valueOf(rdpConfig.getLocalTunnelPort())), gbc);
        }
        
        // Resolution
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        infoPanel.add(new JLabel("分辨率:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(rdpConfig.getResolutionString()), gbc);
        
        // Status
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        infoPanel.add(new JLabel("状态:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(statusLabel, gbc);
        
        add(infoPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(connectBtn);
        buttonPanel.add(disconnectBtn);
        buttonPanel.add(progressBar);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Connect to RDP
     */
    public void connect() {
        connectBtn.setEnabled(false);
        progressBar.setVisible(true);
        setStatus("正在连接...");
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Connect SSH first if tunnel is needed
                if (rdpConfig.isUseSshTunnel() && sshConfig != null) {
                    sshSession = new SSHSession(sshConfig);
                    sshSession.connect();
                }
                
                // Connect RDP
                rdpSession = new RDPSession(rdpConfig, sshSession);
                rdpSession.addListener(RDPPanel.this);
                rdpSession.connect();
                
                return null;
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                try {
                    get();
                    disconnectBtn.setEnabled(true);
                } catch (Exception e) {
                    connectBtn.setEnabled(true);
                    setStatus("连接失败: " + e.getMessage());
                    logger.error("RDP connection failed", e);
                    JOptionPane.showMessageDialog(RDPPanel.this, 
                        "RDP连接失败: " + e.getMessage(), 
                        "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
    
    /**
     * Disconnect RDP
     */
    public void disconnect() {
        if (rdpSession != null) {
            rdpSession.disconnect();
            rdpSession = null;
        }
        
        if (sshSession != null) {
            sshSession.disconnect();
            sshSession = null;
        }
        
        disconnectBtn.setEnabled(false);
        connectBtn.setEnabled(true);
        setStatus("已断开");
    }
    
    private void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    @Override
    public void onRDPEvent(RDPSession.RDPEvent event, String message) {
        SwingUtilities.invokeLater(() -> {
            switch (event) {
                case CONNECTING:
                    setStatus("正在连接...");
                    break;
                case CONNECTED:
                    setStatus("已连接");
                    break;
                case DISCONNECTED:
                    setStatus("已断开");
                    disconnectBtn.setEnabled(false);
                    connectBtn.setEnabled(true);
                    break;
                case ERROR:
                    setStatus("错误: " + message);
                    break;
            }
        });
    }
    
    /**
     * Close panel
     */
    public void close() {
        disconnect();
    }
}
