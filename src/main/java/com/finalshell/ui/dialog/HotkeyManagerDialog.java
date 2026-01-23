package com.finalshell.ui.dialog;

import com.finalshell.hotkey.HotkeyConfig;
import com.finalshell.hotkey.HotkeyManager;
import com.finalshell.ui.table.HotkeyTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 快捷键管理对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class HotkeyManagerDialog extends JDialog {
    
    private HotkeyTable hotkeyTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton resetButton;
    private JButton closeButton;
    
    private HotkeyManager hotkeyManager;
    
    public HotkeyManagerDialog(Frame owner) {
        super(owner, "快捷键管理", true);
        this.hotkeyManager = HotkeyManager.getInstance();
        initUI();
        loadHotkeys();
        setSize(500, 400);
        setLocationRelativeTo(owner);
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        hotkeyTable = new HotkeyTable();
        JScrollPane scrollPane = new JScrollPane(hotkeyTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addButton = new JButton("添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        resetButton = new JButton("重置");
        
        addButton.addActionListener(e -> addHotkey());
        editButton.addActionListener(e -> editHotkey());
        deleteButton.addActionListener(e -> deleteHotkey());
        resetButton.addActionListener(e -> resetHotkeys());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetButton);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void loadHotkeys() {
        List<HotkeyConfig> hotkeys = hotkeyManager.getHotkeys();
        hotkeyTable.setHotkeys(hotkeys);
    }
    
    private void addHotkey() {
        AddHotkeyDialog dialog = new AddHotkeyDialog((Frame) getOwner());
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            HotkeyConfig config = dialog.getConfig();
            hotkeyManager.addHotkey(config);
            loadHotkeys();
        }
    }
    
    private void editHotkey() {
        HotkeyConfig selected = hotkeyTable.getSelectedHotkey();
        if (selected != null) {
            AddHotkeyDialog dialog = new AddHotkeyDialog((Frame) getOwner(), selected);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                hotkeyManager.updateHotkey(selected);
                loadHotkeys();
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要编辑的快捷键", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteHotkey() {
        HotkeyConfig selected = hotkeyTable.getSelectedHotkey();
        if (selected != null) {
            int result = JOptionPane.showConfirmDialog(this, 
                "确定删除该快捷键?", "确认", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                hotkeyManager.removeHotkey(selected);
                loadHotkeys();
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要删除的快捷键", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetHotkeys() {
        int result = JOptionPane.showConfirmDialog(this, 
            "确定重置所有快捷键为默认值?", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            hotkeyManager.resetToDefault();
            loadHotkeys();
        }
    }
}
