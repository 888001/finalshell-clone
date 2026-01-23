package com.finalshell.ui.dialog;

import com.finalshell.terminal.QuickCommand;

import javax.swing.*;
import java.awt.*;

/**
 * 创建命令对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class CreateCmdDialog extends JDialog {
    
    private JTextField nameField;
    private JTextArea commandField;
    private JCheckBox enterCheck;
    private JButton okButton;
    private JButton cancelButton;
    
    private QuickCommand command;
    private boolean confirmed = false;
    
    public CreateCmdDialog(Frame owner) {
        this(owner, null);
    }
    
    public CreateCmdDialog(Frame owner, QuickCommand cmd) {
        super(owner, cmd == null ? "新建命令" : "编辑命令", true);
        this.command = cmd != null ? cmd : new QuickCommand();
        initUI();
        loadData();
    }
    
    private void initUI() {
        setSize(400, 250);
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
        nameField = new JTextField(25);
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("命令:"), gbc);
        
        gbc.gridx = 1; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        commandField = new JTextArea(5, 25);
        commandField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(commandField);
        formPanel.add(scrollPane, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        enterCheck = new JCheckBox("自动回车", true);
        formPanel.add(enterCheck, gbc);
        
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
        if (command != null) {
            nameField.setText(command.getName() != null ? command.getName() : "");
            commandField.setText(command.getCommand() != null ? command.getCommand() : "");
            enterCheck.setSelected(command.isAutoEnter());
        }
    }
    
    private void confirm() {
        String name = nameField.getText().trim();
        String cmd = commandField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入命令名称", "提示", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        if (cmd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入命令内容", "提示", JOptionPane.WARNING_MESSAGE);
            commandField.requestFocus();
            return;
        }
        
        command.setName(name);
        command.setCommand(cmd);
        command.setAutoEnter(enterCheck.isSelected());
        
        confirmed = true;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public QuickCommand getCommand() {
        return command;
    }
}
