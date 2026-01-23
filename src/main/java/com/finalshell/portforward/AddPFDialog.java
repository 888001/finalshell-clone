package com.finalshell.portforward;

import javax.swing.*;
import java.awt.*;

/**
 * 添加端口转发对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AddPFDialog extends JDialog {
    
    private JTextField nameField;
    private JComboBox<String> typeCombo;
    private JTextField localHostField;
    private JTextField localPortField;
    private JTextField remoteHostField;
    private JTextField remotePortField;
    private JTextArea descArea;
    private JButton okButton;
    private JButton cancelButton;
    
    private boolean confirmed;
    private PFRule rule;
    
    public AddPFDialog(Window owner) {
        this(owner, null);
    }
    
    public AddPFDialog(Window owner, PFRule rule) {
        super(owner, rule == null ? "添加端口转发" : "编辑端口转发", ModalityType.APPLICATION_MODAL);
        this.rule = rule != null ? rule : new PFRule();
        initUI();
        loadData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 350);
        setLocationRelativeTo(getOwner());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("类型:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        typeCombo = new JComboBox<>(new String[]{"本地转发", "远程转发", "动态转发"});
        formPanel.add(typeCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("本地主机:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        localHostField = new JTextField("127.0.0.1");
        formPanel.add(localHostField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("本地端口:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        localPortField = new JTextField();
        formPanel.add(localPortField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("远程主机:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        remoteHostField = new JTextField("127.0.0.1");
        formPanel.add(remoteHostField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("远程端口:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        remotePortField = new JTextField();
        formPanel.add(remotePortField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        descArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(descArea), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("确定");
        cancelButton = new JButton("取消");
        
        okButton.addActionListener(e -> confirm());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadData() {
        nameField.setText(rule.getName());
        typeCombo.setSelectedIndex(rule.getType());
        localHostField.setText(rule.getLocalHost());
        localPortField.setText(rule.getLocalPort() > 0 ? String.valueOf(rule.getLocalPort()) : "");
        remoteHostField.setText(rule.getRemoteHost());
        remotePortField.setText(rule.getRemotePort() > 0 ? String.valueOf(rule.getRemotePort()) : "");
        descArea.setText(rule.getDescription());
    }
    
    private void confirm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入名称");
            return;
        }
        
        try {
            rule.setName(nameField.getText().trim());
            rule.setType(typeCombo.getSelectedIndex());
            rule.setLocalHost(localHostField.getText().trim());
            rule.setLocalPort(Integer.parseInt(localPortField.getText().trim()));
            rule.setRemoteHost(remoteHostField.getText().trim());
            rule.setRemotePort(Integer.parseInt(remotePortField.getText().trim()));
            rule.setDescription(descArea.getText());
            
            confirmed = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "端口必须是数字");
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public PFRule getRule() {
        return rule;
    }
}
