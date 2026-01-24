package com.finalshell.ui.dialog;

import com.finalshell.hotkey.HotkeyConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 添加快捷键对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class AddHotkeyDialog extends JDialog {
    
    private JComboBox<String> actionCombo;
    private JTextField hotkeyField;
    private JCheckBox ctrlCheckbox;
    private JCheckBox altCheckbox;
    private JCheckBox shiftCheckbox;
    private JButton confirmButton;
    private JButton cancelButton;
    
    private HotkeyConfig config;
    private boolean confirmed = false;
    private int keyCode = 0;
    
    private static final String[] ACTIONS = {
        "新建连接", "打开连接", "关闭标签页", "复制", "粘贴",
        "全选", "查找", "清屏", "断开连接", "重新连接",
        "新建终端", "全屏", "设置"
    };
    
    public AddHotkeyDialog(Frame owner) {
        super(owner, "添加快捷键", true);
        initUI();
        setSize(350, 250);
        setLocationRelativeTo(owner);
    }
    
    public AddHotkeyDialog(Frame owner, HotkeyConfig config) {
        this(owner);
        this.config = config;
        setTitle("编辑快捷键");
        loadConfig();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("功能:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        actionCombo = new JComboBox<>(ACTIONS);
        mainPanel.add(actionCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("修饰键:"), gbc);
        gbc.gridx = 1;
        JPanel modPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ctrlCheckbox = new JCheckBox("Ctrl");
        altCheckbox = new JCheckBox("Alt");
        shiftCheckbox = new JCheckBox("Shift");
        modPanel.add(ctrlCheckbox);
        modPanel.add(altCheckbox);
        modPanel.add(shiftCheckbox);
        mainPanel.add(modPanel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("按键:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hotkeyField = new JTextField(10);
        hotkeyField.setEditable(false);
        hotkeyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyCode = e.getKeyCode();
                hotkeyField.setText(KeyEvent.getKeyText(keyCode));
            }
        });
        mainPanel.add(hotkeyField, gbc);
        
        row++;
        gbc.gridx = 1; gbc.gridy = row;
        mainPanel.add(new JLabel("按下键盘上的按键进行设置"), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmButton = new JButton("确定");
        cancelButton = new JButton("取消");
        
        confirmButton.addActionListener(e -> confirm());
        cancelButton.addActionListener(e -> cancel());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadConfig() {
        if (config != null) {
            actionCombo.setSelectedItem(config.getActionName());
            ctrlCheckbox.setSelected(config.isCtrl());
            altCheckbox.setSelected(config.isAlt());
            shiftCheckbox.setSelected(config.isShift());
            keyCode = config.getKeyCode();
            if (keyCode > 0) {
                hotkeyField.setText(KeyEvent.getKeyText(keyCode));
            }
        }
    }
    
    private void confirm() {
        if (keyCode == 0) {
            JOptionPane.showMessageDialog(this, "请设置快捷键", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (config == null) {
            config = new HotkeyConfig();
        }
        
        config.setActionName((String) actionCombo.getSelectedItem());
        config.setCtrl(ctrlCheckbox.isSelected());
        config.setAlt(altCheckbox.isSelected());
        config.setShift(shiftCheckbox.isSelected());
        config.setKeyCode(keyCode);
        
        confirmed = true;
        dispose();
    }
    
    private void cancel() {
        confirmed = false;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public HotkeyConfig getConfig() {
        return config;
    }
    
    public HotkeyConfig getHotkey() {
        return config;
    }
}
