package com.finalshell.sftp;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.List;

/**
 * FTP文件树拖拽处理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class FtpTreeTransferHandler extends TransferHandler {
    
    private FtpFileTree fileTree;
    
    public FtpTreeTransferHandler(FtpFileTree fileTree) {
        this.fileTree = fileTree;
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        
        if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return true;
        }
        
        return support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
    
    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        
        try {
            Transferable transferable = support.getTransferable();
            
            if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                @SuppressWarnings("unchecked")
                List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                
                for (File file : files) {
                    fileTree.uploadFile(file);
                }
                return true;
            }
            
            if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JTree) {
            JTree tree = (JTree) c;
            TreePath[] paths = tree.getSelectionPaths();
            if (paths != null && paths.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (TreePath path : paths) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(path.getLastPathComponent().toString());
                }
                return new StringSelection(sb.toString());
            }
        }
        return null;
    }
    
    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == MOVE) {
            fileTree.refresh();
        }
    }
}
