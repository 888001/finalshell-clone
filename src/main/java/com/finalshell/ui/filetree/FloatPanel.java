package com.finalshell.ui.filetree;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.ui.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 浮动操作面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FileTree_UI_DeepAnalysis.md - FloatPanel
 */
public class FloatPanel extends JPanel {
    
    private FileTree fileTree;
    private DefaultMutableTreeNode currentNode;
    private Object nodeValue;
    
    private JButton connectButton;
    private JButton settingsButton;
    private JButton removeButton;
    
    private VFile currentFile;
    private VDir currentDir;
    private int currentType;
    
    private static final int TYPE_FILE = 1;
    private static final int TYPE_DIR = 2;
    
    public FloatPanel(FileTree fileTree) {
        this.fileTree = fileTree;
        initUI();
    }
    
    private void initUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        setOpaque(false);
        setVisible(false);
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        setBackground(new Color(250, 250, 250, 240));
        
        // 连接按钮
        connectButton = createButton("\u25B6"); // 播放符号
        connectButton.setToolTipText("连接");
        connectButton.addActionListener(e -> connectToServer());
        
        // 设置按钮
        settingsButton = createButton("\u2699"); // 齿轮符号
        settingsButton.setToolTipText("设置");
        settingsButton.addActionListener(e -> openSettings());
        
        // 删除按钮
        removeButton = createButton("\u2716"); // X符号
        removeButton.setToolTipText("删除");
        removeButton.addActionListener(e -> removeNode());
        removeButton.setForeground(new Color(200, 50, 50));
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(20, 20));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setForeground(new Color(100, 100, 100));
        return button;
    }
    
    public void showForNode(DefaultMutableTreeNode node, Rectangle bounds) {
        this.currentNode = node;
        this.nodeValue = node.getUserObject();
        
        // 移除之前的按钮
        removeAll();
        
        if (nodeValue instanceof VFile) {
            currentFile = (VFile) nodeValue;
            currentType = TYPE_FILE;
            
            // 文件节点显示连接、设置、删除按钮
            add(connectButton);
            add(settingsButton);
            add(removeButton);
        } else if (nodeValue instanceof VDir) {
            currentDir = (VDir) nodeValue;  
            currentType = TYPE_DIR;
            
            // 文件夹节点只显示设置、删除按钮
            add(settingsButton);
            add(removeButton);
        }
        
        // 设置位置和可见性
        setBounds(bounds.x, bounds.y, bounds.width, 25);
        setVisible(true);
        revalidate();
        repaint();
    }
    
    public void hide() {
        setVisible(false);
    }
    
    private void connectToServer() {
        if (currentFile != null && fileTree.getOpenPanel() != null) {
            ConnectConfig config = ConfigManager.getInstance().getConnectionById(currentFile.getId());
            if (config != null) {
                fileTree.getOpenPanel().openConnection(config);
            }
        }
    }
    
    private void openSettings() {
        if (currentNode != null) {
            fileTree.startEditingAtPath(new TreePath(currentNode.getPath()));
        }
    }
    
    private void removeNode() {
        if (currentNode != null) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "确定要删除吗？", 
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                if (nodeValue instanceof VFile) {
                    VFile file = (VFile) nodeValue;
                    ConfigManager.getInstance().deleteConnection(file.getId());
                } else if (nodeValue instanceof VDir) {
                    VDir dir = (VDir) nodeValue;
                    ConfigManager.getInstance().removeFolder(dir.getId());
                }
                
                fileTree.refresh();
                hide();
            }
        }
    }
}
