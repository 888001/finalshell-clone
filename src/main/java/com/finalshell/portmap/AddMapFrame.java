package com.finalshell.portmap;

import javax.swing.*;
import java.awt.*;

/**
 * 添加端口映射对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AddMapFrame extends JDialog {
    
    private JTextField nameField;
    private JComboBox<String> typeCombo;
    private JTextField localHostField;
    private JTextField localPortField;
    private JTextField remoteHostField;
    private JTextField remotePortField;
    private JTextArea descArea;
    private boolean confirmed;
    private MapRule rule;
    
    public AddMapFrame(Window owner) {
        this(owner, null);
    }
    
    public AddMapFrame(Window owner, MapRule rule) {
        super(owner, rule == null ? "添加映射规则" : "编辑映射规则", ModalityType.APPLICATION_MODAL);
        this.rule = rule != null ? rule : new MapRule();
        initUI();
        if (rule != null) {
            loadRule(rule);
        }
        pack();
        setLocationRelativeTo(owner);
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3;
        nameField = new JTextField(25);
        formPanel.add(nameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("类型:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3;
        typeCombo = new JComboBox<>(new String[]{"本地 -> 远程", "远程 -> 本地", "动态(SOCKS)"});
        formPanel.add(typeCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("本地地址:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        localHostField = new JTextField("127.0.0.1");
        formPanel.add(localHostField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        localPortField = new JTextField("8080");
        formPanel.add(localPortField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("远程地址:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        remoteHostField = new JTextField();
        formPanel.add(remoteHostField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        remotePortField = new JTextField("80");
        formPanel.add(remotePortField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("备注:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        descArea = new JTextArea(3, 25);
        formPanel.add(new JScrollPane(descArea), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        
        okButton.addActionListener(e -> {
            if (validateInput()) {
                saveRule();
                confirmed = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadRule(MapRule rule) {
        nameField.setText(rule.getName());
        localHostField.setText(rule.getLocalHost());
        localPortField.setText(String.valueOf(rule.getLocalPort()));
        remoteHostField.setText(rule.getRemoteHost());
        remotePortField.setText(String.valueOf(rule.getRemotePort()));
        descArea.setText(rule.getDescription());
    }
    
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入名称");
            return false;
        }
        try {
            Integer.parseInt(localPortField.getText().trim());
            Integer.parseInt(remotePortField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "端口必须是数字");
            return false;
        }
        return true;
    }
    
    private void saveRule() {
        rule.setName(nameField.getText().trim());
        rule.setType((String) typeCombo.getSelectedItem());
        rule.setLocalHost(localHostField.getText().trim());
        rule.setLocalPort(Integer.parseInt(localPortField.getText().trim()));
        rule.setRemoteHost(remoteHostField.getText().trim());
        rule.setRemotePort(Integer.parseInt(remotePortField.getText().trim()));
        rule.setDescription(descArea.getText().trim());
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public MapRule getRule() {
        return rule;
    }
}
