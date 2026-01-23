package com.finalshell.ui.filetree;

import java.awt.datatransfer.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础可传输对象
 * 支持文本和文件列表的拖放传输
 */
public class BasicTransferable implements Transferable, ClipboardOwner {
    
    private String plainData;
    private String htmlData;
    private List<File> fileList;
    
    private static DataFlavor[] textFlavors;
    private static DataFlavor[] fileFlavors;
    private static DataFlavor[] allFlavors;
    
    static {
        try {
            textFlavors = new DataFlavor[] {
                DataFlavor.stringFlavor,
                DataFlavor.plainTextFlavor
            };
            
            fileFlavors = new DataFlavor[] {
                DataFlavor.javaFileListFlavor
            };
            
            List<DataFlavor> all = new ArrayList<>();
            for (DataFlavor f : textFlavors) all.add(f);
            for (DataFlavor f : fileFlavors) all.add(f);
            allFlavors = all.toArray(new DataFlavor[0]);
        } catch (Exception e) {
            textFlavors = new DataFlavor[0];
            fileFlavors = new DataFlavor[0];
            allFlavors = new DataFlavor[0];
        }
    }
    
    public BasicTransferable(String plainData) {
        this(plainData, null, null);
    }
    
    public BasicTransferable(List<File> fileList) {
        this(null, null, fileList);
    }
    
    public BasicTransferable(String plainData, String htmlData, List<File> fileList) {
        this.plainData = plainData;
        this.htmlData = htmlData;
        this.fileList = fileList;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        List<DataFlavor> flavors = new ArrayList<>();
        
        if (plainData != null || htmlData != null) {
            for (DataFlavor f : textFlavors) {
                flavors.add(f);
            }
        }
        
        if (fileList != null && !fileList.isEmpty()) {
            for (DataFlavor f : fileFlavors) {
                flavors.add(f);
            }
        }
        
        return flavors.toArray(new DataFlavor[0]);
    }
    
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor f : getTransferDataFlavors()) {
            if (f.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(DataFlavor.stringFlavor)) {
            return plainData;
        } else if (flavor.equals(DataFlavor.plainTextFlavor)) {
            return plainData != null ? new StringReader(plainData) : null;
        } else if (flavor.equals(DataFlavor.javaFileListFlavor)) {
            return fileList;
        }
        throw new UnsupportedFlavorException(flavor);
    }
    
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
    
    public String getPlainData() {
        return plainData;
    }
    
    public String getHtmlData() {
        return htmlData;
    }
    
    public List<File> getFileList() {
        return fileList;
    }
    
    public boolean hasText() {
        return plainData != null || htmlData != null;
    }
    
    public boolean hasFiles() {
        return fileList != null && !fileList.isEmpty();
    }
}
