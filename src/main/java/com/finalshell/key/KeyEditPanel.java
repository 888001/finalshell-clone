package com.finalshell.key;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 密钥编辑面板
 * 用于编辑SSH密钥的详细信息
 */
public class KeyEditPanel extends JPanel {
    
    private JTextField nameField;
    private JTextField privateKeyField;
    private JTextField publicKeyField;
    private JPasswordField passphraseField;
    private JTextArea commentArea;
    private JComboBox<String> typeCombo;
    
    private KeyInfo keyInfo;
    
    public KeyEditPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 名称
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("名称:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField(25);
        add(nameField, gbc);
        
        // 类型
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(new JLabel("类型:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        typeCombo = new JComboBox<>(new String[]{"RSA", "DSA", "ECDSA", "ED25519"});
        add(typeCombo, gbc);
        
        // 私钥文件
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(new JLabel("私钥文件:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel privateKeyPanel = new JPanel(new BorderLayout(5, 0));
        privateKeyField = new JTextField();
        privateKeyPanel.add(privateKeyField, BorderLayout.CENTER);
        JButton browsePrivateBtn = new JButton("...");
        browsePrivateBtn.setPreferredSize(new Dimension(30, 25));
        browsePrivateBtn.addActionListener(e -> browsePrivateKey());
        privateKeyPanel.add(browsePrivateBtn, BorderLayout.EAST);
        add(privateKeyPanel, gbc);
        
        // 公钥文件
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(new JLabel("公钥文件:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel publicKeyPanel = new JPanel(new BorderLayout(5, 0));
        publicKeyField = new JTextField();
        publicKeyPanel.add(publicKeyField, BorderLayout.CENTER);
        JButton browsePublicBtn = new JButton("...");
        browsePublicBtn.setPreferredSize(new Dimension(30, 25));
        browsePublicBtn.addActionListener(e -> browsePublicKey());
        publicKeyPanel.add(browsePublicBtn, BorderLayout.EAST);
        add(publicKeyPanel, gbc);
        
        // 密码
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        passphraseField = new JPasswordField();
        add(passphraseField, gbc);
        
        // 备注
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("备注:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        commentArea = new JTextArea(4, 25);
        commentArea.setLineWrap(true);
        add(new JScrollPane(commentArea), gbc);
    }
    
    private void browsePrivateKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择私钥文件");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            privateKeyField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void browsePublicKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择公钥文件");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            publicKeyField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    public void setKeyInfo(KeyInfo info) {
        this.keyInfo = info;
        if (info != null) {
            nameField.setText(info.getName());
            typeCombo.setSelectedItem(info.getType());
            privateKeyField.setText(info.getPrivateKeyPath());
            publicKeyField.setText(info.getPublicKeyPath());
            passphraseField.setText(info.getPassphrase());
            commentArea.setText(info.getComment());
        } else {
            clearFields();
        }
    }
    
    public KeyInfo getKeyInfo() {
        if (keyInfo == null) {
            keyInfo = new KeyInfo();
        }
        keyInfo.setName(nameField.getText().trim());
        keyInfo.setType((String) typeCombo.getSelectedItem());
        keyInfo.setPrivateKeyPath(privateKeyField.getText().trim());
        keyInfo.setPublicKeyPath(publicKeyField.getText().trim());
        keyInfo.setPassphrase(new String(passphraseField.getPassword()));
        keyInfo.setComment(commentArea.getText().trim());
        return keyInfo;
    }
    
    public void clearFields() {
        nameField.setText("");
        typeCombo.setSelectedIndex(0);
        privateKeyField.setText("");
        publicKeyField.setText("");
        passphraseField.setText("");
        commentArea.setText("");
    }
    
    public boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入密钥名称", "验证错误", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        if (privateKeyField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择私钥文件", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        File privateKey = new File(privateKeyField.getText().trim());
        if (!privateKey.exists()) {
            JOptionPane.showMessageDialog(this, "私钥文件不存在", "验证错误", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
