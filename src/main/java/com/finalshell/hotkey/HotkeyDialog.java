package com.finalshell.hotkey;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 热键设置对话框
 */
public class HotkeyDialog extends JDialog {
    private final HotkeyManager hotkeyManager;
    
    private JTable hotkeyTable;
    private DefaultTableModel tableModel;
    private JButton resetBtn;
    private JButton applyBtn;
    
    private List<HotkeyManager.HotkeyBinding> editedBindings;
    
    public HotkeyDialog(Window owner) {
        super(owner, "热键设置", ModalityType.APPLICATION_MODAL);
        this.hotkeyManager = HotkeyManager.getInstance();
        
        setSize(500, 400);
        setLocationRelativeTo(owner);
        
        initComponents();
        loadBindings();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 说明
        JLabel infoLabel = new JLabel("双击热键列修改快捷键，按ESC清除热键");
        mainPanel.add(infoLabel, BorderLayout.NORTH);
        
        // 热键表格
        String[] columns = {"功能", "热键"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        
        hotkeyTable = new JTable(tableModel);
        hotkeyTable.setRowHeight(25);
        hotkeyTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        hotkeyTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        
        // 热键编辑器
        hotkeyTable.getColumnModel().getColumn(1).setCellEditor(new HotkeyEditor());
        
        JScrollPane scrollPane = new JScrollPane(hotkeyTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        resetBtn = new JButton("恢复默认");
        applyBtn = new JButton("应用");
        JButton cancelBtn = new JButton("取消");
        
        resetBtn.addActionListener(e -> resetToDefault());
        applyBtn.addActionListener(e -> apply());
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(resetBtn);
        buttonPanel.add(applyBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void loadBindings() {
        tableModel.setRowCount(0);
        editedBindings = new ArrayList<>();
        
        for (HotkeyManager.HotkeyBinding b : hotkeyManager.getAll()) {
            tableModel.addRow(new Object[]{b.getName(), b.getKeyText()});
            editedBindings.add(new HotkeyManager.HotkeyBinding(
                b.getId(), b.getName(), b.getKeyCode(), b.getModifiers()));
        }
    }
    
    private void resetToDefault() {
        int result = JOptionPane.showConfirmDialog(this,
            "确定要恢复所有热键为默认设置吗?",
            "确认",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            hotkeyManager.resetToDefault();
            loadBindings();
        }
    }
    
    private void apply() {
        // 检查冲突
        for (int i = 0; i < editedBindings.size(); i++) {
            HotkeyManager.HotkeyBinding b = editedBindings.get(i);
            for (int j = i + 1; j < editedBindings.size(); j++) {
                HotkeyManager.HotkeyBinding other = editedBindings.get(j);
                if (b.getKeyCode() == other.getKeyCode() && 
                    b.getModifiers() == other.getModifiers() &&
                    b.getKeyCode() != 0) {
                    JOptionPane.showMessageDialog(this,
                        "热键冲突: " + b.getName() + " 和 " + other.getName(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        
        // 应用更改
        for (HotkeyManager.HotkeyBinding b : editedBindings) {
            hotkeyManager.update(b.getId(), b.getKeyCode(), b.getModifiers());
        }
        
        JOptionPane.showMessageDialog(this, "热键设置已保存", "成功", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
    
    /**
     * 热键编辑器
     */
    private class HotkeyEditor extends AbstractCellEditor implements TableCellEditor {
        private JTextField field;
        private int keyCode;
        private int modifiers;
        
        public HotkeyEditor() {
            field = new JTextField();
            field.setEditable(false);
            field.setBackground(Color.WHITE);
            
            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();
                    
                    // 忽略修饰键本身
                    if (code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_SHIFT ||
                        code == KeyEvent.VK_ALT || code == KeyEvent.VK_META) {
                        return;
                    }
                    
                    // ESC 清除热键
                    if (code == KeyEvent.VK_ESCAPE) {
                        keyCode = 0;
                        modifiers = 0;
                        field.setText("");
                        stopCellEditing();
                        return;
                    }
                    
                    // 记录热键
                    modifiers = e.getModifiersEx();
                    keyCode = code;
                    
                    // 显示热键文本
                    StringBuilder sb = new StringBuilder();
                    if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) sb.append("Ctrl+");
                    if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) sb.append("Shift+");
                    if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) sb.append("Alt+");
                    sb.append(KeyEvent.getKeyText(code));
                    field.setText(sb.toString());
                    
                    e.consume();
                }
                
                @Override
                public void keyReleased(KeyEvent e) {
                    // 释放后结束编辑
                    if (keyCode != 0) {
                        stopCellEditing();
                    }
                }
            });
        }
        
        @Override
        public Object getCellEditorValue() {
            return field.getText();
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            field.setText(value != null ? value.toString() : "");
            
            // 更新编辑后的绑定
            if (row < editedBindings.size()) {
                HotkeyManager.HotkeyBinding b = editedBindings.get(row);
                keyCode = b.getKeyCode();
                modifiers = b.getModifiers();
            }
            
            return field;
        }
        
        @Override
        public boolean stopCellEditing() {
            int row = hotkeyTable.getEditingRow();
            if (row >= 0 && row < editedBindings.size()) {
                HotkeyManager.HotkeyBinding b = editedBindings.get(row);
                b.setKeyCode(keyCode);
                b.setModifiers(modifiers);
            }
            return super.stopCellEditing();
        }
    }
    
    /**
     * 显示对话框
     */
    public static void show(Window owner) {
        HotkeyDialog dialog = new HotkeyDialog(owner);
        dialog.setVisible(true);
    }
}
