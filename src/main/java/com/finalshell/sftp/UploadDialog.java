package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * 上传对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class UploadDialog extends JDialog {
    
    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private JButton addButton;
    private JButton removeButton;
    private JButton clearButton;
    private JTextField remotePathField;
    private JButton uploadButton;
    private JButton cancelButton;
    private JCheckBox overwriteCheckbox;
    
    private String remotePath;
    private boolean confirmed = false;
    
    public UploadDialog(Frame owner) {
        super(owner, "上传文件", true);
        initUI();
        setSize(450, 400);
        setLocationRelativeTo(owner);
    }
    
    public UploadDialog(Frame owner, String remotePath) {
        this(owner);
        this.remotePath = remotePath;
        remotePathField.setText(remotePath);
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        pathPanel.add(new JLabel("远程路径:"), BorderLayout.WEST);
        remotePathField = new JTextField();
        pathPanel.add(remotePathField, BorderLayout.CENTER);
        
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof File) {
                    File file = (File) value;
                    setText(file.getName() + " (" + formatSize(file.length()) + ")");
                }
                return this;
            }
        });
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("待上传文件"));
        
        JPanel fileButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("添加");
        removeButton = new JButton("移除");
        clearButton = new JButton("清空");
        
        addButton.addActionListener(e -> addFiles());
        removeButton.addActionListener(e -> removeSelected());
        clearButton.addActionListener(e -> clearFiles());
        
        fileButtonPanel.add(addButton);
        fileButtonPanel.add(removeButton);
        fileButtonPanel.add(clearButton);
        
        JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        overwriteCheckbox = new JCheckBox("覆盖已存在文件", true);
        optionPanel.add(overwriteCheckbox);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(fileButtonPanel, BorderLayout.SOUTH);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(optionPanel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        uploadButton = new JButton("上传");
        cancelButton = new JButton("取消");
        
        uploadButton.addActionListener(e -> upload());
        cancelButton.addActionListener(e -> cancel());
        
        buttonPanel.add(uploadButton);
        buttonPanel.add(cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        mainPanel.add(pathPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void addFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            for (File file : files) {
                if (!listModel.contains(file)) {
                    listModel.addElement(file);
                }
            }
        }
    }
    
    private void removeSelected() {
        int[] indices = fileList.getSelectedIndices();
        for (int i = indices.length - 1; i >= 0; i--) {
            listModel.remove(indices[i]);
        }
    }
    
    private void clearFiles() {
        listModel.clear();
    }
    
    private void upload() {
        if (listModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请添加要上传的文件", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        remotePath = remotePathField.getText().trim();
        if (remotePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入远程路径", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        confirmed = true;
        dispose();
    }
    
    private void cancel() {
        confirmed = false;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public List<File> getFiles() {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            files.add(listModel.get(i));
        }
        return files;
    }
    
    public String getRemotePath() {
        return remotePath;
    }
    
    public boolean isOverwrite() {
        return overwriteCheckbox.isSelected();
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024L * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
}
