package com.finalshell.key;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SSH密钥管理对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SecretKey_WebView_DeepAnalysis.md
 */
public class KeyManagerDialog extends JDialog implements SecretKeyManager.KeyChangeListener {
    
    private final SecretKeyManager keyManager;
    private JTable keyTable;
    private DefaultTableModel tableModel;
    private JButton importBtn;
    private JButton generateBtn;
    private JButton exportBtn;
    private JButton deleteBtn;
    private JButton renameBtn;
    
    public KeyManagerDialog(Window owner) {
        super(owner, "SSH密钥管理", ModalityType.APPLICATION_MODAL);
        this.keyManager = SecretKeyManager.getInstance();
        
        initComponents();
        loadKeys();
        
        keyManager.addKeyChangeListener(this);
        
        setSize(650, 450);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 表格
        String[] columns = {"名称", "类型", "指纹", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        keyTable = new JTable(tableModel);
        keyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        keyTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        keyTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        keyTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        keyTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        keyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showKeyDetails();
                }
            }
        });
        
        keyTable.getSelectionModel().addListSelectionListener(e -> updateButtonState());
        
        JScrollPane scrollPane = new JScrollPane(keyTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        importBtn = new JButton("导入密钥");
        importBtn.addActionListener(e -> importKey());
        buttonPanel.add(importBtn);
        
        generateBtn = new JButton("生成密钥");
        generateBtn.addActionListener(e -> generateKey());
        buttonPanel.add(generateBtn);
        
        exportBtn = new JButton("导出");
        exportBtn.addActionListener(e -> exportKey());
        exportBtn.setEnabled(false);
        buttonPanel.add(exportBtn);
        
        renameBtn = new JButton("重命名");
        renameBtn.addActionListener(e -> renameKey());
        renameBtn.setEnabled(false);
        buttonPanel.add(renameBtn);
        
        deleteBtn = new JButton("删除");
        deleteBtn.addActionListener(e -> deleteKey());
        deleteBtn.setEnabled(false);
        buttonPanel.add(deleteBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 信息面板
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("说明"));
        JTextArea infoText = new JTextArea(
            "SSH密钥用于无密码登录服务器。\n" +
            "• 支持RSA、DSA、ECDSA、ED25519密钥类型\n" +
            "• 可导入现有密钥或生成新密钥\n" +
            "• 在连接配置中选择密钥进行认证"
        );
        infoText.setEditable(false);
        infoText.setBackground(getBackground());
        infoText.setFont(infoText.getFont().deriveFont(12f));
        infoPanel.add(infoText);
        infoPanel.setPreferredSize(new Dimension(0, 80));
        add(infoPanel, BorderLayout.NORTH);
    }
    
    private void loadKeys() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (SecretKey key : keyManager.getKeys()) {
            tableModel.addRow(new Object[]{
                key.getName(),
                key.getKeyTypeString(),
                key.getFingerprint(),
                sdf.format(new Date(key.getCreateTime()))
            });
        }
    }
    
    private void updateButtonState() {
        boolean selected = keyTable.getSelectedRow() >= 0;
        exportBtn.setEnabled(selected);
        deleteBtn.setEnabled(selected);
        renameBtn.setEnabled(selected);
    }
    
    private void importKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择SSH私钥文件");
        chooser.setFileFilter(new FileNameExtensionFilter("SSH私钥 (*.pem, *.key, id_rsa)", "pem", "key", ""));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            
            String name = JOptionPane.showInputDialog(this, "请输入密钥名称:", file.getName());
            if (name == null || name.trim().isEmpty()) return;
            
            String password = JOptionPane.showInputDialog(this, "请输入密钥密码(如果有):");
            
            try {
                keyManager.importKey(file, name, password);
                JOptionPane.showMessageDialog(this, "密钥导入成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | JSchException e) {
                JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void generateKey() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField nameField = new JTextField("my_key");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"RSA", "DSA", "ECDSA"});
        JComboBox<Integer> sizeCombo = new JComboBox<>(new Integer[]{2048, 4096, 1024});
        JPasswordField passwordField = new JPasswordField();
        
        panel.add(new JLabel("名称:"));
        panel.add(nameField);
        panel.add(new JLabel("类型:"));
        panel.add(typeCombo);
        panel.add(new JLabel("密钥长度:"));
        panel.add(sizeCombo);
        panel.add(new JLabel("密码(可选):"));
        panel.add(passwordField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "生成SSH密钥", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入密钥名称", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int type;
            switch ((String) typeCombo.getSelectedItem()) {
                case "DSA": type = KeyPair.DSA; break;
                case "ECDSA": type = KeyPair.ECDSA; break;
                default: type = KeyPair.RSA;
            }
            
            int keySize = (Integer) sizeCombo.getSelectedItem();
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) password = null;
            
            try {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                keyManager.generateKey(name, type, keySize, password);
                JOptionPane.showMessageDialog(this, "密钥生成成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (JSchException e) {
                JOptionPane.showMessageDialog(this, "生成失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }
    
    private void exportKey() {
        int row = keyTable.getSelectedRow();
        if (row < 0) return;
        
        SecretKey key = keyManager.getKeys().get(row);
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出SSH私钥");
        chooser.setSelectedFile(new File(key.getName()));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                keyManager.exportKey(key.getId(), chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "导出成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteKey() {
        int row = keyTable.getSelectedRow();
        if (row < 0) return;
        
        SecretKey key = keyManager.getKeys().get(row);
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定要删除密钥 \"" + key.getName() + "\" 吗?",
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            keyManager.removeKey(key.getId());
        }
    }
    
    private void renameKey() {
        int row = keyTable.getSelectedRow();
        if (row < 0) return;
        
        SecretKey key = keyManager.getKeys().get(row);
        
        String newName = JOptionPane.showInputDialog(this, "请输入新名称:", key.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            key.setName(newName.trim());
            keyManager.updateKey(key);
        }
    }
    
    private void showKeyDetails() {
        int row = keyTable.getSelectedRow();
        if (row < 0) return;
        
        SecretKey key = keyManager.getKeys().get(row);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String details = String.format(
            "名称: %s\n" +
            "类型: %s\n" +
            "指纹: %s\n" +
            "创建时间: %s\n" +
            "修改时间: %s",
            key.getName(),
            key.getKeyTypeString(),
            key.getFingerprint(),
            sdf.format(new Date(key.getCreateTime())),
            sdf.format(new Date(key.getModifyTime()))
        );
        
        JOptionPane.showMessageDialog(this, details, "密钥详情", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void onKeyChanged() {
        loadKeys();
    }
    
    @Override
    public void dispose() {
        keyManager.removeKeyChangeListener(this);
        super.dispose();
    }
    
    /**
     * 显示密钥管理对话框
     */
    public static void show(Window owner) {
        KeyManagerDialog dialog = new KeyManagerDialog(owner);
        dialog.setVisible(true);
    }
}
