package com.finalshell.layout;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 布局管理对话框
 */
public class LayoutDialog extends JDialog {
    private final JFrame parentFrame;
    private final LayoutManager layoutManager;
    
    private JTable layoutTable;
    private DefaultTableModel tableModel;
    private JButton saveBtn;
    private JButton loadBtn;
    private JButton deleteBtn;
    private JButton renameBtn;
    
    public LayoutDialog(JFrame parent) {
        super(parent, "布局管理", true);
        this.parentFrame = parent;
        this.layoutManager = LayoutManager.getInstance();
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        
        initComponents();
        loadLayouts();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 布局表格
        String[] columns = {"名称", "描述", "创建时间", "更新时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        layoutTable = new JTable(tableModel);
        layoutTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layoutTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        layoutTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        layoutTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        layoutTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(layoutTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        saveBtn = new JButton("保存当前");
        loadBtn = new JButton("加载");
        deleteBtn = new JButton("删除");
        renameBtn = new JButton("重命名");
        
        loadBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        renameBtn.setEnabled(false);
        
        saveBtn.addActionListener(e -> saveCurrentLayout());
        loadBtn.addActionListener(e -> loadSelectedLayout());
        deleteBtn.addActionListener(e -> deleteSelectedLayout());
        renameBtn.addActionListener(e -> renameSelectedLayout());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(renameBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 表格选择监听
        layoutTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = layoutTable.getSelectedRow() >= 0;
            loadBtn.setEnabled(selected);
            deleteBtn.setEnabled(selected);
            renameBtn.setEnabled(selected);
        });
        
        // 双击加载
        layoutTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedLayout();
                }
            }
        });
        
        setContentPane(mainPanel);
    }
    
    private void loadLayouts() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (LayoutConfig config : layoutManager.getAllLayouts()) {
            tableModel.addRow(new Object[]{
                config.getName(),
                config.getDescription() != null ? config.getDescription() : "",
                sdf.format(new Date(config.getCreateTime())),
                sdf.format(new Date(config.getUpdateTime()))
            });
        }
    }
    
    private void saveCurrentLayout() {
        String name = JOptionPane.showInputDialog(this, "请输入布局名称:", "保存布局", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;
        
        name = name.trim();
        
        // 检查是否已存在
        if (layoutManager.getLayout(name) != null) {
            int result = JOptionPane.showConfirmDialog(this,
                "布局 '" + name + "' 已存在，是否覆盖?",
                "确认覆盖",
                JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) return;
        }
        
        // 询问描述
        String desc = JOptionPane.showInputDialog(this, "布局描述 (可选):", "保存布局", JOptionPane.PLAIN_MESSAGE);
        
        // 捕获当前布局
        LayoutConfig config = layoutManager.captureFromWindow(parentFrame, name);
        if (desc != null && !desc.trim().isEmpty()) {
            config.setDescription(desc.trim());
        }
        
        layoutManager.saveLayout(config);
        loadLayouts();
        
        JOptionPane.showMessageDialog(this, "布局已保存", "成功", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void loadSelectedLayout() {
        int row = layoutTable.getSelectedRow();
        if (row < 0) return;
        
        String name = (String) tableModel.getValueAt(row, 0);
        LayoutConfig config = layoutManager.getLayout(name);
        
        if (config != null) {
            layoutManager.applyToWindow(parentFrame, config);
            JOptionPane.showMessageDialog(this, "布局已应用", "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteSelectedLayout() {
        int row = layoutTable.getSelectedRow();
        if (row < 0) return;
        
        String name = (String) tableModel.getValueAt(row, 0);
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定要删除布局 '" + name + "' 吗?",
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            layoutManager.deleteLayout(name);
            loadLayouts();
        }
    }
    
    private void renameSelectedLayout() {
        int row = layoutTable.getSelectedRow();
        if (row < 0) return;
        
        String oldName = (String) tableModel.getValueAt(row, 0);
        String newName = JOptionPane.showInputDialog(this, "新名称:", oldName);
        
        if (newName == null || newName.trim().isEmpty() || newName.equals(oldName)) return;
        
        LayoutConfig config = layoutManager.getLayout(oldName);
        if (config != null) {
            layoutManager.deleteLayout(oldName);
            config.setName(newName.trim());
            layoutManager.saveLayout(config);
            loadLayouts();
        }
    }
    
    /**
     * 显示对话框
     */
    public static void show(JFrame parent) {
        LayoutDialog dialog = new LayoutDialog(parent);
        dialog.setVisible(true);
    }
}
