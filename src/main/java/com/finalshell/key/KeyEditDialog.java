package com.finalshell.key;

import javax.swing.*;
import java.awt.*;

/**
 * 密钥编辑对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class KeyEditDialog extends JDialog {
    
    private JTextField nameField;
    private JTextField keyFileField;
    private JPasswordField passphraseField;
    private JComboBox<String> typeCombo;
    private JTextArea commentArea;
    private JButton browseButton;
    private JButton generateButton;
    private boolean confirmed;
    private KeyInfo keyInfo;
    
    public KeyEditDialog(Window owner) {
        this(owner, null);
    }
    
    public KeyEditDialog(Window owner, KeyInfo key) {
        super(owner, key == null ? "添加密钥" : "编辑密钥", ModalityType.APPLICATION_MODAL);
        this.keyInfo = key != null ? key : new KeyInfo();
        initUI();
        if (key != null) {
            loadKeyInfo(key);
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
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        nameField = new JTextField(25);
        formPanel.add(nameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("类型:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        typeCombo = new JComboBox<>(new String[]{"RSA", "DSA", "ECDSA", "Ed25519"});
        formPanel.add(typeCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("密钥文件:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        keyFileField = new JTextField();
        formPanel.add(keyFileField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        browseButton = new JButton("...");
        browseButton.addActionListener(e -> browseKeyFile());
        formPanel.add(browseButton, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("密钥密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        passphraseField = new JPasswordField();
        formPanel.add(passphraseField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("备注:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        commentArea = new JTextArea(3, 25);
        formPanel.add(new JScrollPane(commentArea), gbc);
        
        row++;
        gbc.gridy = row;
        gbc.gridx = 1; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        generateButton = new JButton("生成新密钥对");
        generateButton.addActionListener(e -> generateKey());
        formPanel.add(generateButton, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        
        okButton.addActionListener(e -> {
            if (validateInput()) {
                saveKeyInfo();
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
    
    private void loadKeyInfo(KeyInfo key) {
        nameField.setText(key.getName());
        keyFileField.setText(key.getKeyFile());
        passphraseField.setText(key.getPassphrase());
        typeCombo.setSelectedItem(key.getType());
        commentArea.setText(key.getComment());
    }
    
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入名称");
            return false;
        }
        return true;
    }
    
    private void saveKeyInfo() {
        keyInfo.setName(nameField.getText().trim());
        keyInfo.setKeyFile(keyFileField.getText().trim());
        keyInfo.setPassphrase(new String(passphraseField.getPassword()));
        keyInfo.setType((String) typeCombo.getSelectedItem());
        keyInfo.setComment(commentArea.getText().trim());
    }
    
    private void browseKeyFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择密钥文件");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            keyFileField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void generateKey() {
        JOptionPane.showMessageDialog(this, "密钥生成功能");
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }
}
