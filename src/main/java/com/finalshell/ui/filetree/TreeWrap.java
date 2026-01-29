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
    private JLayeredPane layeredPane;
    
    public TreeWrap(OpenPanel openPanel, boolean forSearch) {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        fileTree = new FileTree(openPanel, forSearch);
        
        scrollPane = new JScrollPane(fileTree);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        floatPanel = new FloatPanel(fileTree);

        layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        layeredPane.setLayout(null);
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(floatPanel, JLayeredPane.PALETTE_LAYER);
        add(layeredPane, BorderLayout.CENTER);

        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                scrollPane.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
            }
        });

        fileTree.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                javax.swing.tree.TreePath path = fileTree.getPathForLocation(e.getX(), e.getY());
                if (path == null) {
                    floatPanel.hide();
                    return;
                }
                javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (!(userObject instanceof com.finalshell.ui.VFile) && !(userObject instanceof com.finalshell.ui.VDir)) {
                    floatPanel.hide();
                    return;
                }

                java.awt.Rectangle rowBounds = fileTree.getPathBounds(path);
                if (rowBounds == null) {
                    floatPanel.hide();
                    return;
                }
                java.awt.Rectangle rect = javax.swing.SwingUtilities.convertRectangle(fileTree, rowBounds, layeredPane);
                rect.width = scrollPane.getViewport().getWidth();
                floatPanel.showForNode(node, rect);
            }
        });

        fileTree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                floatPanel.hide();
            }
        });

        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> floatPanel.hide());
        scrollPane.getHorizontalScrollBar().addAdjustmentListener(e -> floatPanel.hide());

        javax.swing.SwingUtilities.invokeLater(() -> {
            scrollPane.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
        });
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
