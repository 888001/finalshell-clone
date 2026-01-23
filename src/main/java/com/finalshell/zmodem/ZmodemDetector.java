package com.finalshell.zmodem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

/**
 * Zmodem检测器 - 检测终端中的rz/sz命令
 */
public class ZmodemDetector {
    private static final Logger logger = LoggerFactory.getLogger(ZmodemDetector.class);
    
    private final InputStream terminalInput;
    private final OutputStream terminalOutput;
    private final ZmodemCallback callback;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private byte[] buffer = new byte[1024];
    private int bufferPos = 0;
    
    private volatile boolean enabled = true;
    private File downloadDirectory = new File(System.getProperty("user.home"), "Downloads");
    
    public ZmodemDetector(InputStream input, OutputStream output, ZmodemCallback callback) {
        this.terminalInput = input;
        this.terminalOutput = output;
        this.callback = callback;
    }
    
    /**
     * 处理终端输出数据，检测Zmodem序列
     */
    public boolean processData(byte[] data, int offset, int length) {
        if (!enabled) return false;
        
        for (int i = offset; i < offset + length; i++) {
            buffer[bufferPos++] = data[i];
            if (bufferPos >= buffer.length) {
                bufferPos = 0;
            }
        }
        
        // 检测sz启动序列 (ZRQINIT)
        if (detectSzStart(data, offset, length)) {
            logger.info("检测到sz命令 (Zmodem发送)");
            startReceive();
            return true;
        }
        
        // 检测rz等待序列
        if (detectRzWaiting(data, offset, length)) {
            logger.info("检测到rz命令 (Zmodem接收)");
            startSend();
            return true;
        }
        
        return false;
    }
    
    private boolean detectSzStart(byte[] data, int offset, int length) {
        // 检测 **\x18B (ZRQINIT)
        byte[] signature = ZmodemProtocol.SZ_SIGNATURE;
        
        for (int i = offset; i <= offset + length - signature.length; i++) {
            boolean match = true;
            for (int j = 0; j < signature.length; j++) {
                if (data[i + j] != signature[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return true;
        }
        return false;
    }
    
    private boolean detectRzWaiting(byte[] data, int offset, int length) {
        // 检测 "rz waiting" 文本
        String text = new String(data, offset, length);
        return text.contains("rz waiting") || text.contains("rz\r\n");
    }
    
    private void startReceive() {
        if (callback != null) {
            callback.onZmodemReceiveStart();
        }
        
        executor.submit(() -> {
            try {
                ZmodemReceiver receiver = new ZmodemReceiver(
                    terminalInput, 
                    terminalOutput, 
                    downloadDirectory,
                    new ZmodemReceiver.ZmodemListener() {
                        @Override
                        public void onFileStart(String fileName, long totalSize) {
                            if (callback != null) {
                                SwingUtilities.invokeLater(() -> 
                                    callback.onFileStart(fileName, totalSize, false));
                            }
                        }
                        
                        @Override
                        public void onProgress(String fileName, long received, long total) {
                            if (callback != null) {
                                SwingUtilities.invokeLater(() -> 
                                    callback.onProgress(fileName, received, total));
                            }
                        }
                        
                        @Override
                        public void onFileComplete(String fileName, File file) {
                            if (callback != null) {
                                SwingUtilities.invokeLater(() -> 
                                    callback.onFileComplete(fileName, file));
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            if (callback != null) {
                                SwingUtilities.invokeLater(() -> 
                                    callback.onError(error));
                            }
                        }
                    }
                );
                
                List<File> files = receiver.receive();
                logger.info("Zmodem接收完成，共{}个文件", files.size());
                
                if (callback != null) {
                    SwingUtilities.invokeLater(() -> callback.onZmodemComplete(files.size()));
                }
                
            } catch (ZmodemException e) {
                logger.error("Zmodem接收失败", e);
                if (callback != null) {
                    SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }
    
    private void startSend() {
        if (callback != null) {
            // 请求用户选择文件
            SwingUtilities.invokeLater(() -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);
                chooser.setDialogTitle("选择要上传的文件 (rz)");
                
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] files = chooser.getSelectedFiles();
                    if (files.length > 0) {
                        sendFiles(files);
                    }
                }
            });
        }
    }
    
    private void sendFiles(File[] files) {
        callback.onZmodemSendStart(files);
        
        executor.submit(() -> {
            try {
                ZmodemSender sender = new ZmodemSender(
                    terminalInput,
                    terminalOutput,
                    new ZmodemReceiver.ZmodemListener() {
                        @Override
                        public void onFileStart(String fileName, long totalSize) {
                            if (callback != null) {
                                SwingUtilities.invokeLater(() -> 
                                    callback.onFileStart(fileName, totalSize, true));
                            }
                        }
                        
                        @Override
                        public void onProgress(String fileName, long sent, long total) {
                            if (callback != null) {
                                SwingUtilities.invokeLater(() -> 
                                    callback.onProgress(fileName, sent, total));
                            }
                        }
                        
                        @Override
                        public void onFileComplete(String fileName, File file) {
                            if (callback != null) {
                                SwingUtilities.invokeLater(() -> 
                                    callback.onFileComplete(fileName, file));
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            if (callback != null) {
                                SwingUtilities.invokeLater(() -> 
                                    callback.onError(error));
                            }
                        }
                    }
                );
                
                sender.send(files);
                logger.info("Zmodem发送完成，共{}个文件", files.length);
                
                if (callback != null) {
                    SwingUtilities.invokeLater(() -> callback.onZmodemComplete(files.length));
                }
                
            } catch (ZmodemException e) {
                logger.error("Zmodem发送失败", e);
                if (callback != null) {
                    SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }
    
    public void setDownloadDirectory(File directory) {
        this.downloadDirectory = directory;
    }
    
    public File getDownloadDirectory() {
        return downloadDirectory;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    /**
     * Zmodem回调接口
     */
    public interface ZmodemCallback {
        void onZmodemReceiveStart();
        void onZmodemSendStart(File[] files);
        void onFileStart(String fileName, long totalSize, boolean isSending);
        void onProgress(String fileName, long transferred, long total);
        void onFileComplete(String fileName, File file);
        void onZmodemComplete(int fileCount);
        void onError(String error);
    }
}
