package com.finalshell.ui.filetree;

import com.finalshell.ui.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

/**
 * 文件树单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FileTree_UI_DeepAnalysis.md - FileTreeCellRenderer
 */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    
    private ImageManager imageManager;
    
    public FileTreeCellRenderer() {
        imageManager = ImageManager.getInstance();
        setOpaque(false);
        setBorderSelectionColor(null);
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            
            if (userObject instanceof VFile) {
                VFile file = (VFile) userObject;
                setText(file.getName());
                setIcon(getIconForFile(file));
            } else if (userObject instanceof VDir) {
                VDir dir = (VDir) userObject;
                setText(dir.getName());
                setIcon(imageManager.getIcon("folder", 16, 16));
            } else if (userObject instanceof String) {
                setText((String) userObject);
                setIcon(imageManager.getIcon("folder", 16, 16));
            }
        }
        
        // 选中样式
        if (sel) {
            setBackground(UIConfig.SELECTION_BG);
            setForeground(UIConfig.SELECTION_FG);
            setOpaque(true);
        } else {
            setBackground(null);
            setForeground(Color.BLACK);
            setOpaque(false);
        }
        
        return this;
    }
    
    private Icon getIconForFile(VFile file) {
        int type = file.getType();
        switch (type) {
            case VFile.TYPE_DIRECTORY:
                return imageManager.getIcon("folder", 16, 16);
            default:
                return imageManager.getIcon("ssh", 16, 16);
        }
    }
}
