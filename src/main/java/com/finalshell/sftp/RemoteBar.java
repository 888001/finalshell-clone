package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 远程文件工具栏
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class RemoteBar extends JPanel {
    
    private JButton homeButton;
    private JButton upButton;
    private JButton refreshButton;
    private JButton newFolderButton;
    private JButton deleteButton;
    private JButton renameButton;
    private JButton downloadButton;
    private FtpPathAF pathField;
    
    public RemoteBar() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 0));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        
        homeButton = createButton("主目录", "home");
        upButton = createButton("上级目录", "up");
        refreshButton = createButton("刷新", "refresh");
        newFolderButton = createButton("新建文件夹", "folder_add");
        deleteButton = createButton("删除", "delete");
        renameButton = createButton("重命名", "rename");
        downloadButton = createButton("下载", "download");
        
        buttonPanel.add(homeButton);
        buttonPanel.add(upButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(newFolderButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(renameButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(downloadButton);
        
        pathField = new FtpPathAF(30);
        
        add(buttonPanel, BorderLayout.WEST);
        add(pathField, BorderLayout.CENTER);
    }
    
    private JButton createButton(String tooltip, String iconName) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        button.setMargin(new Insets(2, 4, 2, 4));
        return button;
    }
    
    public JButton getHomeButton() {
        return homeButton;
    }
    
    public JButton getUpButton() {
        return upButton;
    }
    
    public JButton getRefreshButton() {
        return refreshButton;
    }
    
    public JButton getNewFolderButton() {
        return newFolderButton;
    }
    
    public JButton getDeleteButton() {
        return deleteButton;
    }
    
    public JButton getRenameButton() {
        return renameButton;
    }
    
    public JButton getDownloadButton() {
        return downloadButton;
    }
    
    public FtpPathAF getPathField() {
        return pathField;
    }
    
    public String getPath() {
        return pathField.getText();
    }
    
    public void setPath(String path) {
        pathField.setText(path);
    }
}
