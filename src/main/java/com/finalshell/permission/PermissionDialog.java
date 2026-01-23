package com.finalshell.permission;

import com.finalshell.ssh.SSHSession;
import com.jcraft.jsch.ChannelExec;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件权限编辑对话框
 */
public class PermissionDialog extends JDialog {
    private final SSHSession session;
    private final String filePath;
    private FilePermission permission;
    
    // 权限复选框
    private JCheckBox ownerRead, ownerWrite, ownerExecute;
    private JCheckBox groupRead, groupWrite, groupExecute;
    private JCheckBox otherRead, otherWrite, otherExecute;
    private JCheckBox setuid, setgid, sticky;
    
    // 所有者/组
    private JTextField ownerField;
    private JTextField groupField;
    
    // 八进制输入
    private JTextField octalField;
    
    // 预览
    private JLabel previewLabel;
    private JLabel commandLabel;
    
    private boolean confirmed = false;
    private boolean recursive = false;
    
    public PermissionDialog(Window owner, SSHSession session, String filePath) {
        super(owner, "修改权限 - " + filePath, ModalityType.APPLICATION_MODAL);
        this.session = session;
        this.filePath = filePath;
        
        setSize(500, 450);
        setLocationRelativeTo(owner);
        setResizable(false);
        
        initComponents();
        loadCurrentPermission();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 文件路径
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(new JLabel("文件: " + filePath), BorderLayout.CENTER);
        mainPanel.add(pathPanel, BorderLayout.NORTH);
        
        // 中间面板
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // 权限矩阵
        JPanel permPanel = new JPanel(new GridBagLayout());
        permPanel.setBorder(BorderFactory.createTitledBorder("权限"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // 表头
        gbc.gridy = 0;
        gbc.gridx = 1; permPanel.add(new JLabel("读取(r)"), gbc);
        gbc.gridx = 2; permPanel.add(new JLabel("写入(w)"), gbc);
        gbc.gridx = 3; permPanel.add(new JLabel("执行(x)"), gbc);
        
        // 所有者
        gbc.gridy = 1;
        gbc.gridx = 0; permPanel.add(new JLabel("所有者:"), gbc);
        gbc.gridx = 1; permPanel.add(ownerRead = new JCheckBox(), gbc);
        gbc.gridx = 2; permPanel.add(ownerWrite = new JCheckBox(), gbc);
        gbc.gridx = 3; permPanel.add(ownerExecute = new JCheckBox(), gbc);
        
        // 组
        gbc.gridy = 2;
        gbc.gridx = 0; permPanel.add(new JLabel("组:"), gbc);
        gbc.gridx = 1; permPanel.add(groupRead = new JCheckBox(), gbc);
        gbc.gridx = 2; permPanel.add(groupWrite = new JCheckBox(), gbc);
        gbc.gridx = 3; permPanel.add(groupExecute = new JCheckBox(), gbc);
        
        // 其他
        gbc.gridy = 3;
        gbc.gridx = 0; permPanel.add(new JLabel("其他:"), gbc);
        gbc.gridx = 1; permPanel.add(otherRead = new JCheckBox(), gbc);
        gbc.gridx = 2; permPanel.add(otherWrite = new JCheckBox(), gbc);
        gbc.gridx = 3; permPanel.add(otherExecute = new JCheckBox(), gbc);
        
        centerPanel.add(permPanel, BorderLayout.NORTH);
        
        // 特殊权限和八进制
        JPanel midPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // 特殊权限
        JPanel specialPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        specialPanel.setBorder(BorderFactory.createTitledBorder("特殊权限"));
        specialPanel.add(setuid = new JCheckBox("Set UID (SUID)"));
        specialPanel.add(setgid = new JCheckBox("Set GID (SGID)"));
        specialPanel.add(sticky = new JCheckBox("Sticky Bit"));
        midPanel.add(specialPanel);
        
        // 八进制和所有者
        JPanel rightPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("数值/所有者"));
        
        JPanel octalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        octalPanel.add(new JLabel("八进制: "));
        octalField = new JTextField(6);
        octalPanel.add(octalField);
        rightPanel.add(octalPanel);
        
        JPanel ownerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ownerPanel.add(new JLabel("所有者: "));
        ownerField = new JTextField(10);
        ownerPanel.add(ownerField);
        rightPanel.add(ownerPanel);
        
        JPanel groupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        groupPanel.add(new JLabel("组:       "));
        groupField = new JTextField(10);
        groupPanel.add(groupField);
        rightPanel.add(groupPanel);
        
        JCheckBox recursiveCheck = new JCheckBox("递归应用到子目录");
        rightPanel.add(recursiveCheck);
        recursiveCheck.addActionListener(e -> recursive = recursiveCheck.isSelected());
        
        midPanel.add(rightPanel);
        centerPanel.add(midPanel, BorderLayout.CENTER);
        
        // 预览
        JPanel previewPanel = new JPanel(new GridLayout(2, 1));
        previewPanel.setBorder(BorderFactory.createTitledBorder("预览"));
        previewLabel = new JLabel("-rwxr-xr-x");
        previewLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        commandLabel = new JLabel("chmod 755 " + filePath);
        commandLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        previewPanel.add(previewLabel);
        previewPanel.add(commandLabel);
        centerPanel.add(previewPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyBtn = new JButton("应用");
        JButton cancelBtn = new JButton("取消");
        
        applyBtn.addActionListener(e -> apply());
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(applyBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // 添加监听器
        ActionListener updateListener = e -> updatePreview();
        ownerRead.addActionListener(updateListener);
        ownerWrite.addActionListener(updateListener);
        ownerExecute.addActionListener(updateListener);
        groupRead.addActionListener(updateListener);
        groupWrite.addActionListener(updateListener);
        groupExecute.addActionListener(updateListener);
        otherRead.addActionListener(updateListener);
        otherWrite.addActionListener(updateListener);
        otherExecute.addActionListener(updateListener);
        setuid.addActionListener(updateListener);
        setgid.addActionListener(updateListener);
        sticky.addActionListener(updateListener);
        
        octalField.addActionListener(e -> parseOctal());
        octalField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                parseOctal();
            }
        });
    }
    
    private void loadCurrentPermission() {
        try {
            String cmd = "stat -c '%a %U %G' '" + filePath.replace("'", "'\\''") + "' 2>/dev/null || " +
                        "ls -ld '" + filePath.replace("'", "'\\''") + "'";
            String output = executeCommand(cmd).trim();
            
            if (output.matches("\\d+ \\S+ \\S+")) {
                // stat format
                String[] parts = output.split(" ");
                int mode = Integer.parseInt(parts[0], 8);
                permission = FilePermission.fromMode(filePath, mode);
                permission.setOwner(parts[1]);
                permission.setGroup(parts[2]);
            } else if (output.startsWith("-") || output.startsWith("d") || output.startsWith("l")) {
                // ls -l format
                String[] parts = output.split("\\s+");
                if (parts.length >= 4) {
                    permission = FilePermission.fromModeString(filePath, parts[0]);
                    permission.setOwner(parts[2]);
                    permission.setGroup(parts[3]);
                }
            }
            
            if (permission != null) {
                updateUIFromPermission();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "无法获取文件权限: " + e.getMessage(),
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUIFromPermission() {
        ownerRead.setSelected(permission.isOwnerRead());
        ownerWrite.setSelected(permission.isOwnerWrite());
        ownerExecute.setSelected(permission.isOwnerExecute());
        groupRead.setSelected(permission.isGroupRead());
        groupWrite.setSelected(permission.isGroupWrite());
        groupExecute.setSelected(permission.isGroupExecute());
        otherRead.setSelected(permission.isOtherRead());
        otherWrite.setSelected(permission.isOtherWrite());
        otherExecute.setSelected(permission.isOtherExecute());
        setuid.setSelected(permission.isSetuid());
        setgid.setSelected(permission.isSetgid());
        sticky.setSelected(permission.isSticky());
        
        ownerField.setText(permission.getOwner() != null ? permission.getOwner() : "");
        groupField.setText(permission.getGroup() != null ? permission.getGroup() : "");
        
        updatePreview();
    }
    
    private void updatePreview() {
        FilePermission perm = buildPermissionFromUI();
        previewLabel.setText(perm.toModeString());
        octalField.setText(perm.getShortOctalString());
        
        StringBuilder cmd = new StringBuilder();
        cmd.append("chmod ");
        if (recursive) cmd.append("-R ");
        cmd.append(perm.getShortOctalString()).append(" ");
        cmd.append(filePath);
        
        String newOwner = ownerField.getText().trim();
        String newGroup = groupField.getText().trim();
        if (!newOwner.isEmpty() || !newGroup.isEmpty()) {
            cmd.append(" && chown ");
            if (recursive) cmd.append("-R ");
            if (!newOwner.isEmpty()) cmd.append(newOwner);
            cmd.append(":");
            if (!newGroup.isEmpty()) cmd.append(newGroup);
            cmd.append(" ").append(filePath);
        }
        
        commandLabel.setText(cmd.toString());
    }
    
    private void parseOctal() {
        try {
            String text = octalField.getText().trim();
            int mode = Integer.parseInt(text, 8);
            FilePermission perm = FilePermission.fromMode(filePath, mode);
            
            ownerRead.setSelected(perm.isOwnerRead());
            ownerWrite.setSelected(perm.isOwnerWrite());
            ownerExecute.setSelected(perm.isOwnerExecute());
            groupRead.setSelected(perm.isGroupRead());
            groupWrite.setSelected(perm.isGroupWrite());
            groupExecute.setSelected(perm.isGroupExecute());
            otherRead.setSelected(perm.isOtherRead());
            otherWrite.setSelected(perm.isOtherWrite());
            otherExecute.setSelected(perm.isOtherExecute());
            setuid.setSelected(perm.isSetuid());
            setgid.setSelected(perm.isSetgid());
            sticky.setSelected(perm.isSticky());
            
            previewLabel.setText(perm.toModeString());
            
        } catch (NumberFormatException e) {
            // 忽略无效输入
        }
    }
    
    private FilePermission buildPermissionFromUI() {
        FilePermission perm = new FilePermission(filePath);
        perm.setOwnerRead(ownerRead.isSelected());
        perm.setOwnerWrite(ownerWrite.isSelected());
        perm.setOwnerExecute(ownerExecute.isSelected());
        perm.setGroupRead(groupRead.isSelected());
        perm.setGroupWrite(groupWrite.isSelected());
        perm.setGroupExecute(groupExecute.isSelected());
        perm.setOtherRead(otherRead.isSelected());
        perm.setOtherWrite(otherWrite.isSelected());
        perm.setOtherExecute(otherExecute.isSelected());
        perm.setSetuid(setuid.isSelected());
        perm.setSetgid(setgid.isSelected());
        perm.setSticky(sticky.isSelected());
        return perm;
    }
    
    private void apply() {
        FilePermission perm = buildPermissionFromUI();
        
        try {
            // 执行chmod
            StringBuilder cmd = new StringBuilder();
            cmd.append("chmod ");
            if (recursive) cmd.append("-R ");
            cmd.append(perm.getShortOctalString()).append(" '");
            cmd.append(filePath.replace("'", "'\\''")).append("'");
            
            String result = executeCommand(cmd.toString());
            if (!result.isEmpty() && result.toLowerCase().contains("error")) {
                throw new Exception(result);
            }
            
            // 执行chown（如果有变更）
            String newOwner = ownerField.getText().trim();
            String newGroup = groupField.getText().trim();
            
            if (!newOwner.isEmpty() || !newGroup.isEmpty()) {
                cmd = new StringBuilder();
                cmd.append("chown ");
                if (recursive) cmd.append("-R ");
                if (!newOwner.isEmpty()) cmd.append(newOwner);
                cmd.append(":");
                if (!newGroup.isEmpty()) cmd.append(newGroup);
                cmd.append(" '").append(filePath.replace("'", "'\\''")).append("'");
                
                result = executeCommand(cmd.toString());
                if (!result.isEmpty() && result.toLowerCase().contains("error")) {
                    throw new Exception(result);
                }
            }
            
            confirmed = true;
            JOptionPane.showMessageDialog(this, "权限修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "权限修改失败: " + e.getMessage(),
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String executeCommand(String command) throws Exception {
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.getSession().openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            
            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();
            channel.connect(10000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(err));
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            while ((line = errReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            
            return sb.toString();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    /**
     * 显示权限编辑对话框
     */
    public static boolean showDialog(Window owner, SSHSession session, String filePath) {
        PermissionDialog dialog = new PermissionDialog(owner, session, filePath);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
}
