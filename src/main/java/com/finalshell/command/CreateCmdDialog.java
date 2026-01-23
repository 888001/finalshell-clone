package com.finalshell.command;

import javax.swing.*;
import java.awt.*;

/**
 * 创建命令对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CreateCmdDialog extends JDialog {
    
    private JTextField nameField;
    private JTextArea commandArea;
    private JTextField descField;
    private JTextField hotkeyField;
    private boolean confirmed;
    private QuickCmd command;
    
    public CreateCmdDialog(Window owner) {
        this(owner, null);
    }
    
    public CreateCmdDialog(Window owner, QuickCmd cmd) {
        super(owner, cmd == null ? "新建命令" : "编辑命令", ModalityType.APPLICATION_MODAL);
        this.command = cmd != null ? cmd : new QuickCmd();
        initUI();
        if (cmd != null) {
            loadCommand(cmd);
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
        gbc.gridx = 1; gbc.weightx = 1.0;
        nameField = new JTextField(30);
        formPanel.add(nameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("命令:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        commandArea = new JTextArea(5, 30);
        commandArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        formPanel.add(new JScrollPane(commandArea), gbc);
        
        row++;
        gbc.gridy = row; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        descField = new JTextField();
        formPanel.add(descField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("快捷键:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        hotkeyField = new JTextField();
        formPanel.add(hotkeyField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        
        okButton.addActionListener(e -> {
            if (validateInput()) {
                saveCommand();
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
    
    private void loadCommand(QuickCmd cmd) {
        nameField.setText(cmd.getName());
        commandArea.setText(cmd.getCommand());
        descField.setText(cmd.getDescription());
        hotkeyField.setText(cmd.getHotkey());
    }
    
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入名称");
            return false;
        }
        if (commandArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入命令");
            return false;
        }
        return true;
    }
    
    private void saveCommand() {
        command.setName(nameField.getText().trim());
        command.setCommand(commandArea.getText().trim());
        command.setDescription(descField.getText().trim());
        command.setHotkey(hotkeyField.getText().trim());
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public QuickCmd getCommand() {
        return command;
    }
}
