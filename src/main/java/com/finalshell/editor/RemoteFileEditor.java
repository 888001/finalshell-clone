package com.finalshell.editor;

import com.finalshell.ssh.SSHSession;
import com.jcraft.jsch.ChannelSftp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 远程文件编辑器
 */
public class RemoteFileEditor extends JDialog implements TextEditor.EditorCallback {
    private static final Logger logger = LoggerFactory.getLogger(RemoteFileEditor.class);
    
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    private final SSHSession session;
    private final String remotePath;
    private TextEditor editor;
    
    public RemoteFileEditor(Window owner, SSHSession session, String remotePath) {
        super(owner, "编辑 - " + remotePath, ModalityType.MODELESS);
        this.session = session;
        this.remotePath = remotePath;
        
        setSize(800, 600);
        setLocationRelativeTo(owner);
        
        initComponents();
        loadFile();
    }
    
    private void initComponents() {
        editor = new TextEditor(this);
        editor.setFilePath(remotePath);
        
        setContentPane(editor);
        
        // 关闭确认
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (confirmClose()) {
                    dispose();
                }
            }
        });
    }
    
    private void loadFile() {
        loadFile(StandardCharsets.UTF_8);
    }
    
    private void loadFile(Charset charset) {
        new Thread(() -> {
            ChannelSftp sftp = null;
            try {
                sftp = (ChannelSftp) session.getSession().openChannel("sftp");
                sftp.connect(10000);
                
                // 检查文件大小
                long size = sftp.stat(remotePath).getSize();
                if (size > MAX_FILE_SIZE) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "文件过大 (" + (size / 1024 / 1024) + "MB)，无法编辑",
                            "错误", JOptionPane.ERROR_MESSAGE);
                        dispose();
                    });
                    return;
                }
                
                // 读取内容
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                sftp.get(remotePath, baos);
                String content = new String(baos.toByteArray(), charset);
                
                SwingUtilities.invokeLater(() -> {
                    editor.setText(content);
                });
                
            } catch (Exception e) {
                logger.error("加载文件失败: {}", remotePath, e);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "加载文件失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                if (sftp != null && sftp.isConnected()) {
                    sftp.disconnect();
                }
            }
        }).start();
    }
    
    @Override
    public void onSave(String path, String content, Charset charset) {
        new Thread(() -> {
            ChannelSftp sftp = null;
            try {
                sftp = (ChannelSftp) session.getSession().openChannel("sftp");
                sftp.connect(10000);
                
                byte[] data = content.getBytes(charset);
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                sftp.put(bais, remotePath, ChannelSftp.OVERWRITE);
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                });
                
            } catch (Exception e) {
                logger.error("保存文件失败: {}", remotePath, e);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "保存失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                if (sftp != null && sftp.isConnected()) {
                    sftp.disconnect();
                }
            }
        }).start();
    }
    
    @Override
    public void onReload(String path, Charset charset) {
        loadFile(charset);
    }
    
    private boolean confirmClose() {
        if (editor.isModified()) {
            int result = JOptionPane.showConfirmDialog(this,
                "文件已修改，是否保存?",
                "确认关闭",
                JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                onSave(remotePath, editor.getText(), StandardCharsets.UTF_8);
                return true;
            } else if (result == JOptionPane.NO_OPTION) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 打开远程文件编辑器
     */
    public static void open(Window owner, SSHSession session, String remotePath) {
        RemoteFileEditor editor = new RemoteFileEditor(owner, session, remotePath);
        editor.setVisible(true);
    }
}
