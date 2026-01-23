package com.finalshell.ui.dialog;

import com.finalshell.config.FolderConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 组/文件夹编辑对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class GroupEditDialog extends JDialog {
    
    private JTextField nameField;
    private JTextArea descField;
    private JButton okButton;
    private JButton cancelButton;
    
    private FolderConfig folderConfig;
    private boolean confirmed = false;
    
    public GroupEditDialog(Frame owner) {
        this(owner, null);
    }
    
    public GroupEditDialog(Frame owner, FolderConfig config) {
        super(owner, config == null ? "新建文件夹" : "编辑文件夹", true);
        this.folderConfig = config != null ? config : new FolderConfig();
        initUI();
        loadData();
    }
    
    private void initUI() {
        setSize(350, 200);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("名称:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("描述:"), gbc);
        
        gbc.gridx = 1; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descField = new JTextArea(3, 20);
        descField.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descField);
        formPanel.add(scrollPane, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        okButton = new JButton("确定");
        okButton.addActionListener(e -> confirm());
        buttonPanel.add(okButton);
        
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        getRootPane().setDefaultButton(okButton);
    }
    
    private void loadData() {
        if (folderConfig != null) {
            nameField.setText(folderConfig.getName() != null ? folderConfig.getName() : "");
            descField.setText(folderConfig.getDescription() != null ? folderConfig.getDescription() : "");
        }
    }
    
    private void confirm() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入文件夹名称", "提示", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        folderConfig.setName(name);
        folderConfig.setDescription(descField.getText().trim());
        
        confirmed = true;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public FolderConfig getFolderConfig() {
        return folderConfig;
    }
}
