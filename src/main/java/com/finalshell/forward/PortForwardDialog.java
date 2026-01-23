package com.finalshell.forward;

import javax.swing.*;
import java.awt.*;

/**
 * Port Forward Dialog - Create new port forwarding rule
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PortForwardDialog extends JDialog {
    
    private final PortForwardManager.ForwardType type;
    
    private JTextField localPortField;
    private JTextField localHostField;
    private JTextField remotePortField;
    private JTextField remoteHostField;
    
    private boolean confirmed = false;
    
    public PortForwardDialog(Window owner, PortForwardManager.ForwardType type) {
        super(owner, getTitle(type), ModalityType.APPLICATION_MODAL);
        this.type = type;
        
        initComponents();
        initLayout();
        
        setSize(400, 200);
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private static String getTitle(PortForwardManager.ForwardType type) {
        switch (type) {
            case LOCAL: return "添加本地端口转发";
            case REMOTE: return "添加远程端口转发";
            case DYNAMIC: return "添加动态端口转发 (SOCKS)";
            default: return "添加端口转发";
        }
    }
    
    private void initComponents() {
        localPortField = new JTextField("0");
        localHostField = new JTextField("localhost");
        remotePortField = new JTextField("0");
        remoteHostField = new JTextField("localhost");
    }
    
    private void initLayout() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        switch (type) {
            case LOCAL:
                // Local: localhost:localPort -> remoteHost:remotePort
                addFormRow(formPanel, gbc, row++, "本地端口:", localPortField);
                addFormRow(formPanel, gbc, row++, "远程主机:", remoteHostField);
                addFormRow(formPanel, gbc, row++, "远程端口:", remotePortField);
                
                // Help text
                JLabel helpLocal = new JLabel("<html><font color='gray'>本地端口转发: 本地程序连接 localhost:本地端口 将被转发到 远程主机:远程端口</font></html>");
                gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
                formPanel.add(helpLocal, gbc);
                break;
                
            case REMOTE:
                // Remote: remotePort -> localHost:localPort
                addFormRow(formPanel, gbc, row++, "远程端口:", remotePortField);
                addFormRow(formPanel, gbc, row++, "本地主机:", localHostField);
                addFormRow(formPanel, gbc, row++, "本地端口:", localPortField);
                
                JLabel helpRemote = new JLabel("<html><font color='gray'>远程端口转发: 远程服务器的端口将被转发到本地主机:本地端口</font></html>");
                gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
                formPanel.add(helpRemote, gbc);
                break;
                
            case DYNAMIC:
                // Dynamic: SOCKS proxy on localPort
                addFormRow(formPanel, gbc, row++, "本地端口:", localPortField);
                localPortField.setText("1080");
                
                JLabel helpDynamic = new JLabel("<html><font color='gray'>动态转发: 在本地端口创建SOCKS代理，支持浏览器等应用代理</font></html>");
                gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
                formPanel.add(helpDynamic, gbc);
                break;
        }
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okBtn = new JButton("确定");
        okBtn.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });
        buttonPanel.add(okBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        getRootPane().setDefaultButton(okBtn);
    }
    
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }
    
    private boolean validateInput() {
        try {
            int localPort = Integer.parseInt(localPortField.getText().trim());
            if (localPort < 0 || localPort > 65535) {
                showError("本地端口必须在 0-65535 之间");
                return false;
            }
            
            if (type != PortForwardManager.ForwardType.DYNAMIC) {
                int remotePort = Integer.parseInt(remotePortField.getText().trim());
                if (remotePort < 0 || remotePort > 65535) {
                    showError("远程端口必须在 0-65535 之间");
                    return false;
                }
                
                String host = type == PortForwardManager.ForwardType.LOCAL ? 
                    remoteHostField.getText().trim() : localHostField.getText().trim();
                if (host.isEmpty()) {
                    showError("主机地址不能为空");
                    return false;
                }
            }
            
            return true;
        } catch (NumberFormatException e) {
            showError("端口必须是数字");
            return false;
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "输入错误", JOptionPane.ERROR_MESSAGE);
    }
    
    // Getters
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public int getLocalPort() {
        return Integer.parseInt(localPortField.getText().trim());
    }
    
    public String getLocalHost() {
        return localHostField.getText().trim();
    }
    
    public int getRemotePort() {
        return Integer.parseInt(remotePortField.getText().trim());
    }
    
    public String getRemoteHost() {
        return remoteHostField.getText().trim();
    }
}
