package com.finalshell.plugin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * 插件管理对话框
 */
public class PluginDialog extends JDialog {
    private final PluginManager pluginManager;
    
    private JTable pluginTable;
    private DefaultTableModel tableModel;
    private JTextArea descArea;
    private JButton enableBtn;
    private JButton disableBtn;
    private JButton installBtn;
    private JButton uninstallBtn;
    
    public PluginDialog(Window owner) {
        super(owner, "插件管理", ModalityType.APPLICATION_MODAL);
        this.pluginManager = PluginManager.getInstance();
        
        setSize(600, 450);
        setLocationRelativeTo(owner);
        
        initComponents();
        loadPlugins();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 插件表格
        String[] columns = {"名称", "版本", "作者", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        pluginTable = new JTable(tableModel);
        pluginTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pluginTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        pluginTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        pluginTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        pluginTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        
        // 状态列渲染
        pluginTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("已启用".equals(value)) {
                    setForeground(new Color(0, 128, 0));
                } else {
                    setForeground(Color.GRAY);
                }
                return this;
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(pluginTable);
        
        // 描述区域
        descArea = new JTextArea(4, 30);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createTitledBorder("插件描述"));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, descScroll);
        splitPane.setResizeWeight(0.7);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        enableBtn = new JButton("启用");
        disableBtn = new JButton("禁用");
        installBtn = new JButton("安装");
        uninstallBtn = new JButton("卸载");
        
        enableBtn.setEnabled(false);
        disableBtn.setEnabled(false);
        uninstallBtn.setEnabled(false);
        
        enableBtn.addActionListener(e -> enablePlugin());
        disableBtn.addActionListener(e -> disablePlugin());
        installBtn.addActionListener(e -> installPlugin());
        uninstallBtn.addActionListener(e -> uninstallPlugin());
        
        buttonPanel.add(enableBtn);
        buttonPanel.add(disableBtn);
        buttonPanel.add(installBtn);
        buttonPanel.add(uninstallBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 表格选择监听
        pluginTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonState();
                showPluginDescription();
            }
        });
        
        setContentPane(mainPanel);
    }
    
    private void loadPlugins() {
        tableModel.setRowCount(0);
        
        for (PluginInfo info : pluginManager.getAllPlugins()) {
            tableModel.addRow(new Object[]{
                info.getName(),
                info.getVersion(),
                info.getAuthor(),
                info.isEnabled() ? "已启用" : "已禁用"
            });
        }
    }
    
    private void updateButtonState() {
        int row = pluginTable.getSelectedRow();
        if (row < 0) {
            enableBtn.setEnabled(false);
            disableBtn.setEnabled(false);
            uninstallBtn.setEnabled(false);
            return;
        }
        
        String status = (String) tableModel.getValueAt(row, 3);
        boolean enabled = "已启用".equals(status);
        
        enableBtn.setEnabled(!enabled);
        disableBtn.setEnabled(enabled);
        uninstallBtn.setEnabled(true);
    }
    
    private void showPluginDescription() {
        int row = pluginTable.getSelectedRow();
        if (row < 0) {
            descArea.setText("");
            return;
        }
        
        String name = (String) tableModel.getValueAt(row, 0);
        for (PluginInfo info : pluginManager.getAllPlugins()) {
            if (info.getName().equals(name)) {
                descArea.setText(info.getDescription() != null ? info.getDescription() : "无描述");
                return;
            }
        }
    }
    
    private String getSelectedPluginId() {
        int row = pluginTable.getSelectedRow();
        if (row < 0) return null;
        
        String name = (String) tableModel.getValueAt(row, 0);
        for (PluginInfo info : pluginManager.getAllPlugins()) {
            if (info.getName().equals(name)) {
                return info.getId();
            }
        }
        return null;
    }
    
    private void enablePlugin() {
        String pluginId = getSelectedPluginId();
        if (pluginId != null) {
            if (pluginManager.enablePlugin(pluginId)) {
                loadPlugins();
                JOptionPane.showMessageDialog(this, "插件已启用", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "启用插件失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void disablePlugin() {
        String pluginId = getSelectedPluginId();
        if (pluginId != null) {
            pluginManager.disablePlugin(pluginId);
            loadPlugins();
            JOptionPane.showMessageDialog(this, "插件已禁用", "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void installPlugin() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JAR文件", "jar"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (pluginManager.installPlugin(file)) {
                loadPlugins();
                JOptionPane.showMessageDialog(this, "插件安装成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "安装插件失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void uninstallPlugin() {
        String pluginId = getSelectedPluginId();
        if (pluginId == null) return;
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定要卸载此插件吗?",
            "确认卸载",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            if (pluginManager.uninstallPlugin(pluginId)) {
                loadPlugins();
                JOptionPane.showMessageDialog(this, "插件已卸载", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "卸载插件失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 显示对话框
     */
    public static void show(Window owner) {
        PluginDialog dialog = new PluginDialog(owner);
        dialog.setVisible(true);
    }
}
