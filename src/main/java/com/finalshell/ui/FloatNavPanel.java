package com.finalshell.ui;

import com.finalshell.network.*;
import com.finalshell.key.KeyManagerDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 浮动导航面板 - 快速访问工具
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FloatNav_Panel_DeepAnalysis.md
 */
public class FloatNavPanel extends JPanel {
    
    private final Window owner;
    
    public FloatNavPanel(Window owner) {
        this.owner = owner;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setBackground(new Color(250, 250, 250));
        initComponents();
    }
    
    private void initComponents() {
        // 标题
        JLabel titleLabel = new JLabel("工具箱", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // 工具按钮网格
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        gridPanel.setOpaque(false);
        
        // 网络工具
        gridPanel.add(createToolButton("Ping", "网络连通测试", e -> showPingTool()));
        gridPanel.add(createToolButton("路由追踪", "Traceroute", e -> showTraceroute()));
        gridPanel.add(createToolButton("端口扫描", "扫描开放端口", e -> showPortScan()));
        
        // 查询工具
        gridPanel.add(createToolButton("DNS查询", "域名解析", e -> showDnsLookup()));
        gridPanel.add(createToolButton("WHOIS", "域名信息查询", e -> showWhois()));
        gridPanel.add(createToolButton("速度测试", "网络带宽测试", e -> showSpeedTest()));
        
        // 管理工具
        gridPanel.add(createToolButton("密钥管理", "SSH密钥", e -> showKeyManager()));
        gridPanel.add(createToolButton("进程管理", "远程进程", e -> showProcessManager()));
        gridPanel.add(createToolButton("系统信息", "服务器信息", e -> showSystemInfo()));
        
        add(gridPanel, BorderLayout.CENTER);
        
        // 底部状态
        JLabel statusLabel = new JLabel("点击工具图标打开", SwingConstants.CENTER);
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private JButton createToolButton(String text, String tooltip, ActionListener action) {
        JButton btn = new JButton("<html><center>" + text + "</center></html>");
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(80, 60));
        btn.setFocusPainted(false);
        btn.addActionListener(action);
        return btn;
    }
    
    private void showPingTool() {
        showToolDialog("Ping测试", new NetworkPanel(null));
    }
    
    private void showTraceroute() {
        showToolDialog("路由追踪", new TraceroutePanel());
    }
    
    private void showPortScan() {
        showToolDialog("端口扫描", new NetworkPanel(null));
    }
    
    private void showDnsLookup() {
        showToolDialog("DNS查询", new NetworkPanel(null));
    }
    
    private void showWhois() {
        showToolDialog("WHOIS查询", new WhoisPanel());
    }
    
    private void showSpeedTest() {
        showToolDialog("网络速度测试", new SpeedTestPanel());
    }
    
    private void showKeyManager() {
        KeyManagerDialog.show(owner);
    }
    
    private void showProcessManager() {
        JOptionPane.showMessageDialog(owner, 
            "请在已连接的会话中使用进程管理功能", 
            "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showSystemInfo() {
        JOptionPane.showMessageDialog(owner, 
            "请在已连接的会话中查看系统信息", 
            "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showToolDialog(String title, JPanel content) {
        JDialog dialog = new JDialog((Frame) owner, title, false);
        dialog.setContentPane(content);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
    
    /**
     * 显示浮动导航对话框
     */
    public static void show(Window owner, int x, int y) {
        JDialog dialog = new JDialog((Frame) owner);
        dialog.setUndecorated(true);
        dialog.setContentPane(new FloatNavPanel(owner));
        dialog.setSize(300, 280);
        dialog.setLocation(x, y);
        
        // 失焦自动关闭
        dialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {}
            
            @Override
            public void windowLostFocus(WindowEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.setVisible(true);
    }
}
