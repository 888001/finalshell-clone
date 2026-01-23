package com.finalshell.ui.filetree;

import com.finalshell.ui.OpenPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 文件树包装器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FileTree_UI_DeepAnalysis.md - TreeWrap
 */
public class TreeWrap extends JPanel {
    
    private FileTree fileTree;
    private FloatPanel floatPanel;
    private JScrollPane scrollPane;
    
    public TreeWrap(OpenPanel openPanel, boolean forSearch) {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        fileTree = new FileTree(openPanel, forSearch);
        
        scrollPane = new JScrollPane(fileTree);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
        
        floatPanel = new FloatPanel(fileTree);
        // 浮动面板添加到LayeredPane
    }
    
    public FileTree getFileTree() {
        return fileTree;
    }
    
    public FloatPanel getFloatPanel() {
        return floatPanel;
    }
    
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
