package com.finalshell.key;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 密钥选择对话框
 * 用于选择已存在的SSH密钥
 */
public class KeyChooseDialog extends JDialog {
    
    private KeyTable keyTable;
    private KeyTableModel tableModel;
    private KeyInfo selectedKey;
    private boolean confirmed = false;
    
    public KeyChooseDialog(Frame owner) {
        super(owner, "选择密钥", true);
        initComponents();
        setSize(500, 400);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        
        // 密钥列表
        tableModel = new KeyTableModel();
        keyTable = new KeyTable(tableModel);
        keyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        keyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    confirmSelection();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(keyTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton selectBtn = new JButton("选择");
        selectBtn.addActionListener(e -> confirmSelection());
        buttonPanel.add(selectBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        buttonPanel.add(cancelBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 加载密钥列表
        loadKeys();
    }
    
    private void loadKeys() {
        List<KeyInfo> keys = com.finalshell.key.KeyManager.getInstance().getAllKeys();
        tableModel.setKeys(keys);
    }
    
    private void confirmSelection() {
        int selectedRow = keyTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = keyTable.convertRowIndexToModel(selectedRow);
            selectedKey = tableModel.getKeyAt(modelRow);
            confirmed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "请选择一个密钥", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public KeyInfo getSelectedKey() {
        return selectedKey;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public static KeyInfo showDialog(Frame owner) {
        KeyChooseDialog dialog = new KeyChooseDialog(owner);
        dialog.setVisible(true);
        return dialog.isConfirmed() ? dialog.getSelectedKey() : null;
    }
}
