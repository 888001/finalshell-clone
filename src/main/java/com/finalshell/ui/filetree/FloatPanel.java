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
        
        // 设置按钮
        settingsButton = createButton("\u2699"); // 齿轮符号
        settingsButton.setToolTipText("设置");
        settingsButton.addActionListener(e -> openSettings());
        add(settingsButton);
        
        // 连接按钮
        connectButton = createButton("\u25B6"); // 播放符号
        connectButton.setToolTipText("连接");
        connectButton.addActionListener(e -> connect());
        add(connectButton);
        
        // 删除按钮
        removeButton = createButton("\u2716"); // X符号
        removeButton.setToolTipText("删除");
        removeButton.addActionListener(e -> remove());
        add(removeButton);
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        button.setMargin(new Insets(2, 4, 2, 4));
        button.setForeground(new Color(130, 130, 130));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(80, 80, 80));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(130, 130, 130));
            }
        });
        
        return button;
    }
    
    public void showForNode(DefaultMutableTreeNode node, Rectangle bounds) {
        this.currentNode = node;
        this.nodeValue = node.getUserObject();
        
        if (nodeValue instanceof VFile) {
            currentFile = (VFile) nodeValue;
            currentType = TYPE_FILE;
            connectButton.setVisible(true);
        } else if (nodeValue instanceof VDir) {
            currentDir = (VDir) nodeValue;
            currentType = TYPE_DIR;
            connectButton.setVisible(false);
        }
        
        setBounds(bounds.x + bounds.width - 80, bounds.y, 80, bounds.height);
        setVisible(true);
    }
    
    public void hide() {
        setVisible(false);
        currentNode = null;
        nodeValue = null;
    }
    
    private void openSettings() {
        if (currentFile != null) {
            ConnectConfig config = ConfigManager.getInstance().getConnectionById(currentFile.getId());
            if (config != null) {
                ConnectionDialog dialog = new ConnectionDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), config);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    fileTree.refreshTree();
                }
            }
        }
        hide();
    }
    
    private void connect() {
        if (currentFile != null) {
            fileTree.fireConnectEvent(currentFile);
        }
        hide();
    }
    
    private void remove() {
        if (currentNode != null) {
            int result = JOptionPane.showConfirmDialog(this,
                "确定要删除吗？", "确认删除",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                DefaultTreeModel model = fileTree.getTreeModel();
                model.removeNodeFromParent(currentNode);
            }
        }
        hide();
    }
}
