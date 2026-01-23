package com.finalshell.hotkey;

import javax.swing.*;
import java.awt.*;
import com.finalshell.ui.table.HotkeyTable;
import com.finalshell.ui.table.HotkeyTableModel;
import com.finalshell.ui.dialog.AddHotkeyDialog;

/**
 * 快捷键面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class HotkeyPanel extends JPanel {
    
    private HotkeyTable hotkeyTable;
    private HotkeyTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton resetButton;
    
    public HotkeyPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tableModel = new HotkeyTableModel();
        hotkeyTable = new HotkeyTable(tableModel);
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
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(resetButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addHotkey() {
        AddHotkeyDialog dialog = new AddHotkeyDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.addHotkey(dialog.getHotkey());
        }
    }
    
    private void editHotkey() {
        int row = hotkeyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个快捷键");
            return;
        }
        HotkeyInfo hotkey = tableModel.getHotkeyAt(row);
        AddHotkeyDialog dialog = new AddHotkeyDialog(SwingUtilities.getWindowAncestor(this), hotkey);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.updateHotkey(row, dialog.getHotkey());
        }
    }
    
    private void deleteHotkey() {
        int row = hotkeyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个快捷键");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "确定删除此快捷键?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            tableModel.removeHotkey(row);
        }
    }
    
    private void resetHotkeys() {
        int result = JOptionPane.showConfirmDialog(this, "确定重置所有快捷键为默认值?", "确认重置", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            tableModel.resetToDefault();
        }
    }
}
