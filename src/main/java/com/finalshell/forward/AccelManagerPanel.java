package com.finalshell.forward;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * SSH加速映射管理面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSHTunnel_UI_DeepAnalysis.md
 */
public class AccelManagerPanel extends JPanel implements MapRuleManager.MapRuleListener {
    
    private final MapRuleManager ruleManager;
    private JTable ruleTable;
    private DefaultTableModel tableModel;
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton helpBtn;
    
    public AccelManagerPanel() {
        this.ruleManager = MapRuleManager.getInstance();
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadRules();
        
        ruleManager.addListener(this);
    }
    
    private void initComponents() {
        // 标题
        JLabel titleLabel = new JLabel("SSH加速/端口映射规则");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        helpBtn = new JButton("帮助");
        helpBtn.addActionListener(e -> showHelp());
        headerPanel.add(helpBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // 规则表格
        String[] columns = {"名称", "本地端口", "目标地址", "目标端口", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ruleTable = new JTable(tableModel);
        ruleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ruleTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        ruleTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        ruleTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        ruleTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        ruleTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        
        ruleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editRule();
                }
            }
        });
        
        ruleTable.getSelectionModel().addListSelectionListener(e -> updateButtonState());
        
        JScrollPane scrollPane = new JScrollPane(ruleTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        addBtn = new JButton("添加");
        addBtn.addActionListener(e -> addRule());
        buttonPanel.add(addBtn);
        
        editBtn = new JButton("修改");
        editBtn.addActionListener(e -> editRule());
        editBtn.setEnabled(false);
        buttonPanel.add(editBtn);
        
        deleteBtn = new JButton("删除");
        deleteBtn.addActionListener(e -> deleteRule());
        deleteBtn.setEnabled(false);
        buttonPanel.add(deleteBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadRules() {
        tableModel.setRowCount(0);
        
        for (MapRule rule : ruleManager.getRules()) {
            tableModel.addRow(new Object[]{
                rule.getName(),
                rule.getLocalPort(),
                rule.getTargetHost(),
                rule.getTargetPort(),
                rule.isEnabled() ? "启用" : "禁用"
            });
        }
    }
    
    private void updateButtonState() {
        boolean selected = ruleTable.getSelectedRow() >= 0;
        editBtn.setEnabled(selected);
        deleteBtn.setEnabled(selected);
    }
    
    private void addRule() {
        MapRuleDialog dialog = new MapRuleDialog(
            SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            MapRule rule = dialog.getRule();
            ruleManager.addRule(rule);
        }
    }
    
    private void editRule() {
        int row = ruleTable.getSelectedRow();
        if (row < 0) return;
        
        MapRule rule = ruleManager.getRules().get(row);
        MapRuleDialog dialog = new MapRuleDialog(
            SwingUtilities.getWindowAncestor(this), rule);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            ruleManager.updateRule(dialog.getRule());
        }
    }
    
    private void deleteRule() {
        int row = ruleTable.getSelectedRow();
        if (row < 0) return;
        
        MapRule rule = ruleManager.getRules().get(row);
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定要删除规则 \"" + rule.getName() + "\" 吗?",
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            ruleManager.removeRule(rule.getId());
        }
    }
    
    private void showHelp() {
        String helpText = 
            "SSH加速/端口映射使用说明：\n\n" +
            "1. 点击\"添加\"按钮创建新规则\n" +
            "2. 设置本地端口和目标地址/端口\n" +
            "3. 通过SSH连接建立隧道后，本地端口将转发到目标\n\n" +
            "常见用途：\n" +
            "• 访问内网数据库 (如 MySQL 3306)\n" +
            "• 访问内网Web服务\n" +
            "• 远程桌面加速\n";
        
        JOptionPane.showMessageDialog(this, helpText, "帮助", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void onRulesChanged() {
        loadRules();
    }
    
    public void cleanup() {
        ruleManager.removeListener(this);
    }
}

/**
 * 映射规则编辑对话框
 */
class MapRuleDialog extends JDialog {
    
    private JTextField nameField;
    private JTextField localPortField;
    private JTextField targetHostField;
    private JTextField targetPortField;
    private JCheckBox enabledBox;
    
    private MapRule rule;
    private boolean confirmed = false;
    
    public MapRuleDialog(Window owner, MapRule existingRule) {
        super(owner, existingRule == null ? "添加映射规则" : "编辑映射规则", 
            ModalityType.APPLICATION_MODAL);
        
        this.rule = existingRule != null ? existingRule : new MapRule();
        
        initComponents();
        
        if (existingRule != null) {
            nameField.setText(existingRule.getName());
            localPortField.setText(String.valueOf(existingRule.getLocalPort()));
            targetHostField.setText(existingRule.getTargetHost());
            targetPortField.setText(String.valueOf(existingRule.getTargetPort()));
            enabledBox.setSelected(existingRule.isEnabled());
        }
        
        setSize(400, 250);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 名称
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);
        
        // 本地端口
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("本地端口:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        localPortField = new JTextField();
        mainPanel.add(localPortField, gbc);
        
        // 目标地址
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("目标地址:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        targetHostField = new JTextField();
        targetHostField.setText("127.0.0.1");
        mainPanel.add(targetHostField, gbc);
        
        // 目标端口
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("目标端口:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        targetPortField = new JTextField();
        mainPanel.add(targetPortField, gbc);
        
        // 启用
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        enabledBox = new JCheckBox("启用此规则", true);
        mainPanel.add(enabledBox, gbc);
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okBtn = new JButton("确定");
        okBtn.addActionListener(e -> {
            if (validateInput()) {
                saveRule();
                confirmed = true;
                dispose();
            }
        });
        buttonPanel.add(okBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入名称", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int localPort = Integer.parseInt(localPortField.getText().trim());
            if (localPort < 1 || localPort > 65535) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "本地端口必须是1-65535之间的数字", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (targetHostField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入目标地址", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int targetPort = Integer.parseInt(targetPortField.getText().trim());
            if (targetPort < 1 || targetPort > 65535) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "目标端口必须是1-65535之间的数字", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void saveRule() {
        rule.setName(nameField.getText().trim());
        rule.setLocalPort(Integer.parseInt(localPortField.getText().trim()));
        rule.setTargetHost(targetHostField.getText().trim());
        rule.setTargetPort(Integer.parseInt(targetPortField.getText().trim()));
        rule.setEnabled(enabledBox.isSelected());
    }
    
    public MapRule getRule() {
        return rule;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
