package com.finalshell.ui.config;

import com.finalshell.hotkey.HotkeyConfig;
import com.finalshell.hotkey.HotkeyManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * 快捷键配置面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: GlobalConfig_UI_DeepAnalysis.md - HotkeyPanel
 */
public class HotkeyConfigPanel extends ConfigPanel {
    
    private JTable hotkeyTable;
    private HotkeyTableModel tableModel;
    private JButton editButton;
    private JButton resetButton;
    
    public HotkeyConfigPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 表格
        tableModel = new HotkeyTableModel();
        hotkeyTable = new JTable(tableModel);
        hotkeyTable.setRowHeight(25);
        hotkeyTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        hotkeyTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        
        add(new JScrollPane(hotkeyTable), BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editButton = new JButton("编辑");
        resetButton = new JButton("重置为默认");
        
        editButton.addActionListener(e -> editSelectedHotkey());
        resetButton.addActionListener(e -> resetToDefaults());
        
        buttonPanel.add(editButton);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        loadDefaultHotkeys();
    }
    
    private void loadDefaultHotkeys() {
        tableModel.addHotkey(new HotkeyConfig(1, "新建标签", InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_T));
        tableModel.addHotkey(new HotkeyConfig(2, "关闭标签", InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_W));
        tableModel.addHotkey(new HotkeyConfig(3, "复制", InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_C));
        tableModel.addHotkey(new HotkeyConfig(4, "粘贴", InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_V));
        tableModel.addHotkey(new HotkeyConfig(5, "查找", InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_F));
        tableModel.addHotkey(new HotkeyConfig(6, "全屏", 0, KeyEvent.VK_F11));
    }
    
    private void editSelectedHotkey() {
        int row = hotkeyTable.getSelectedRow();
        if (row < 0) return;
        
        HotkeyConfig config = tableModel.getHotkeyAt(row);
        
        // 简单的热键输入对话框
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "编辑快捷键", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        
        JLabel label = new JLabel("请按下新的快捷键...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(label, BorderLayout.CENTER);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_SHIFT && 
                    e.getKeyCode() != KeyEvent.VK_CONTROL && 
                    e.getKeyCode() != KeyEvent.VK_ALT) {
                    config.setFromKeyEvent(e);
                    tableModel.fireTableRowsUpdated(row, row);
                    dialog.dispose();
                }
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void resetToDefaults() {
        tableModel.clear();
        loadDefaultHotkeys();
    }
    
    @Override
    public void apply() {
        HotkeyManager manager = HotkeyManager.getInstance();
        for (HotkeyConfig config : tableModel.getAllHotkeys()) {
            manager.updateHotkey(config);
        }
        manager.save();
    }
    
    @Override
    public void reset() {
        resetToDefaults();
    }
    
    /**
     * 快捷键表格模型
     */
    private static class HotkeyTableModel extends AbstractTableModel {
        private final java.util.List<HotkeyConfig> hotkeys = new ArrayList<>();
        private final String[] columns = {"功能", "快捷键"};
        
        public void addHotkey(HotkeyConfig config) {
            hotkeys.add(config);
            fireTableRowsInserted(hotkeys.size() - 1, hotkeys.size() - 1);
        }
        
        public HotkeyConfig getHotkeyAt(int row) {
            return hotkeys.get(row);
        }
        
        public void clear() {
            hotkeys.clear();
            fireTableDataChanged();
        }
        
        public java.util.List<HotkeyConfig> getAllHotkeys() {
            return new ArrayList<>(hotkeys);
        }
        
        @Override
        public int getRowCount() { return hotkeys.size(); }
        
        @Override
        public int getColumnCount() { return columns.length; }
        
        @Override
        public String getColumnName(int col) { return columns[col]; }
        
        @Override
        public Object getValueAt(int row, int col) {
            HotkeyConfig config = hotkeys.get(row);
            return col == 0 ? config.getName() : config.getKeyText();
        }
    }
}
