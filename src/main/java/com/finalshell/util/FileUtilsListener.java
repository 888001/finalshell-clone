package com.finalshell.util;

import java.io.File;

/**
 * 文件操作监听器
 */
public interface FileUtilsListener {
    void onFileCreated(File file);
    void onFileDeleted(File file);
    void onFileModified(File file);
    void onFileCopied(File file);
    void onFileMoved(File file);
}
