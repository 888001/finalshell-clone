package com.finalshell.sftp;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.util.List;

/**
 * File table transfer handler
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FileTableTransferHandler extends TransferHandler {
    
    private FileTransferListener listener;
    
    public interface FileTransferListener {
        void onFilesDropped(List<File> files, int dropAction);
        void onFilesDragged(List<RemoteFile> files);
    }
    
    public FileTableTransferHandler() {
    }
    
    public FileTableTransferHandler(FileTransferListener listener) {
        this.listener = listener;
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        
        try {
            Transferable t = support.getTransferable();
            List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            
            if (listener != null) {
                listener.onFilesDropped(files, support.getDropAction());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JTable) {
            JTable table = (JTable) c;
            int[] rows = table.getSelectedRows();
            if (rows.length > 0) {
                return new StringSelection("file_transfer");
            }
        }
        return null;
    }
    
    public void setListener(FileTransferListener listener) {
        this.listener = listener;
    }
}