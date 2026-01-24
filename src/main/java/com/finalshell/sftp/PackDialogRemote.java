package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 远程打包对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PackDialogRemote extends JDialog {
    
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JTextField outputField;
    private JComboBox<String> formatCombo;
    private JButton packButton;
    private JButton cancelButton;
    
    private String outputPath;
    private boolean confirmed = false;
    
    public PackDialogRemote(Frame owner) {
        super(owner, "远程打包", true);
        initUI();
        setSize(450, 350);
        setLocationRelativeTo(owner);
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("待打包文件"));
        
        JPanel outputPanel = new JPanel(new BorderLayout(5, 0));
        outputPanel.add(new JLabel("输出路径:"), BorderLayout.WEST);
        outputField = new JTextField();
        outputPanel.add(outputField, BorderLayout.CENTER);
        
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.add(new JLabel("格式:"));
        formatCombo = new JComboBox<>(new String[]{"tar.gz", "zip", "tar"});
        formatPanel.add(formatCombo);
        
        JPanel optionPanel = new JPanel(new BorderLayout());
        optionPanel.add(outputPanel, BorderLayout.NORTH);
        optionPanel.add(formatPanel, BorderLayout.SOUTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        packButton = new JButton("打包");
        cancelButton = new JButton("取消");
        
        packButton.addActionListener(e -> doPack());
        cancelButton.addActionListener(e -> cancel());
        
        buttonPanel.add(packButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(optionPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    public void setFiles(List<String> files) {
        listModel.clear();
        for (String file : files) {
            listModel.addElement(file);
        }
    }
    
    private void doPack() {
        outputPath = outputField.getText().trim();
        if (outputPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请指定输出路径", "提示", JOptionPane.WARNING_MESSAGE);
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
    
    public String getOutputPath() {
        return outputPath;
    }
    
    public String getFormat() {
        return (String) formatCombo.getSelectedItem();
    }
    
    public List<String> getFiles() {
        List<String> files = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            files.add(listModel.get(i));
        }
        return files;
    }
    
    public String getPackCommand() {
        String format = getFormat();
        StringBuilder cmd = new StringBuilder();
        
        if ("tar.gz".equals(format)) {
            cmd.append("tar -czvf ").append(outputPath);
        } else if ("tar".equals(format)) {
            cmd.append("tar -cvf ").append(outputPath);
        } else if ("zip".equals(format)) {
            cmd.append("zip -r ").append(outputPath);
        }
        
        for (String file : getFiles()) {
            cmd.append(" ").append(file);
        }
        
        return cmd.toString();
    }
}
