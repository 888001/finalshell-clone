package com.finalshell.terminal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Quick Command Dialog - Add/Edit quick command
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class QuickCommandDialog extends JDialog {
    
    private final QuickCommand command;
    private boolean confirmed = false;
    
    private JTextField nameField;
    private JTextArea commandArea;
    private JTextField descField;
    private JTextField categoryField;
    private JTextField shortcutField;
    private JCheckBox sendEnterCheck;
    
    public QuickCommandDialog(Window owner, QuickCommand command) {
        super(owner, command == null ? "添加快捷命令" : "编辑快捷命令", ModalityType.APPLICATION_MODAL);
        this.command = command != null ? command : new QuickCommand();
        
        initComponents();
        initLayout();
        initListeners();
        loadCommand();
        
        setSize(400, 350);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        nameField = new JTextField(20);
        commandArea = new JTextArea(3, 20);
        commandArea.setLineWrap(true);
        descField = new JTextField(20);
        categoryField = new JTextField(20);
        shortcutField = new JTextField(10);
        sendEnterCheck = new JCheckBox("执行后发送回车", true);
    }
    
    private void initLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        
        // Command
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("命令:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(commandArea), gbc);
        
        // Description
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(descField, gbc);
        
        // Category
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("分类:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(categoryField, gbc);
        
        // Shortcut
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("快捷键:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(shortcutField, gbc);
        
        // Send enter
        row++;
        gbc.gridx = 1; gbc.gridy = row;
        formPanel.add(sendEnterCheck, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("确定");
        JButton cancelBtn = new JButton("取消");
        
        okBtn.addActionListener(e -> onOK());
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void initListeners() {
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void loadCommand() {
        if (command.getName() != null) {
            nameField.setText(command.getName());
            commandArea.setText(command.getCommand());
            descField.setText(command.getDescription());
            categoryField.setText(command.getCategory());
            shortcutField.setText(command.getShortcut());
            sendEnterCheck.setSelected(command.isSendEnter());
        }
    }
    
    private void onOK() {
        String name = nameField.getText().trim();
        String cmd = commandArea.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入名称", "验证错误", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        if (cmd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入命令", "验证错误", JOptionPane.WARNING_MESSAGE);
            commandArea.requestFocus();
            return;
        }
        
        command.setName(name);
        command.setCommand(cmd);
        command.setDescription(descField.getText().trim());
        command.setCategory(categoryField.getText().trim());
        command.setShortcut(shortcutField.getText().trim());
        command.setSendEnter(sendEnterCheck.isSelected());
        
        confirmed = true;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public QuickCommand getResult() {
        return command;
    }
}
