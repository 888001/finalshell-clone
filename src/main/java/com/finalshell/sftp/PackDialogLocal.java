package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * 本地打包对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PackDialogLocal extends JDialog {
    
    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private JTextField outputField;
    private JComboBox<String> formatCombo;
    private JButton packButton;
    private JButton cancelButton;
    
    private File outputFile;
    private boolean confirmed = false;
    
    public PackDialogLocal(Frame owner) {
        super(owner, "打包文件", true);
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
        outputPanel.add(new JLabel("输出文件:"), BorderLayout.WEST);
        outputField = new JTextField();
        JButton browseButton = new JButton("浏览");
        browseButton.addActionListener(e -> browseOutput());
        outputPanel.add(outputField, BorderLayout.CENTER);
        outputPanel.add(browseButton, BorderLayout.EAST);
        
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.add(new JLabel("格式:"));
        formatCombo = new JComboBox<>(new String[]{"zip", "tar.gz", "tar"});
        formatPanel.add(formatCombo);
        
        JPanel optionPanel = new JPanel(new BorderLayout());
        optionPanel.add(outputPanel, BorderLayout.NORTH);
        optionPanel.add(formatPanel, BorderLayout.SOUTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        packButton = new JButton("打包");
        cancelButton = new JButton("取消");
        
        packButton.addActionListener(e -> pack());
        cancelButton.addActionListener(e -> cancel());
        
        buttonPanel.add(packButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(optionPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    public void setFiles(List<File> files) {
        listModel.clear();
        for (File file : files) {
            listModel.addElement(file);
        }
    }
    
    private void browseOutput() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void pack() {
        String output = outputField.getText().trim();
        if (output.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请指定输出文件", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        outputFile = new File(output);
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
    
    public File getOutputFile() {
        return outputFile;
    }
    
    public String getFormat() {
        return (String) formatCombo.getSelectedItem();
    }
    
    public List<File> getFiles() {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            files.add(listModel.get(i));
        }
        return files;
    }
}
