package com.finalshell.editor;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * 编辑器面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Editor_Event_DeepAnalysis.md - EditorPanel
 */
public class EditorPanel extends JPanel {
    
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private UndoManager undoManager;
    private String filePath;
    private long lastModified;
    private boolean modified = false;
    private int fontSize = 14;
    
    public EditorPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, fontSize));
        textArea.setTabSize(4);
        
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(e -> {
            undoManager.addEdit(e.getEdit());
            modified = true;
        });
        
        // 行号 (简单实现)
        JTextArea lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(240, 240, 240));
        lineNumbers.setEditable(false);
        lineNumbers.setFont(textArea.getFont());
        
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
            
            private void updateLineNumbers() {
                int lines = textArea.getLineCount();
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= lines; i++) {
                    sb.append(i).append("\n");
                }
                lineNumbers.setText(sb.toString());
            }
        });
        
        scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * 加载文件
     */
    public void loadFile(String path) {
        this.filePath = path;
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            textArea.setText(content);
            textArea.setCaretPosition(0);
            
            lastModified = new File(path).lastModified();
            modified = false;
            undoManager.discardAllEdits();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "无法打开文件: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 保存文件
     */
    public boolean save() {
        if (filePath == null) {
            return false;
        }
        return saveAs(filePath);
    }
    
    /**
     * 另存为
     */
    public boolean saveAs(String path) {
        try {
            Files.write(Paths.get(path), textArea.getText().getBytes(StandardCharsets.UTF_8));
            this.filePath = path;
            this.lastModified = new File(path).lastModified();
            this.modified = false;
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "保存失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * 检查文件是否被外部修改
     */
    public void checkFileModified() {
        if (filePath == null) return;
        
        File file = new File(filePath);
        if (file.exists() && file.lastModified() > lastModified) {
            int result = JOptionPane.showConfirmDialog(this,
                "文件已被外部修改，是否重新加载？", 
                "文件已修改", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                loadFile(filePath);
            } else {
                lastModified = file.lastModified();
            }
        }
    }
    
    public void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }
    
    public void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }
    
    public void cut() { textArea.cut(); }
    public void copy() { textArea.copy(); }
    public void paste() { textArea.paste(); }
    
    public void zoomIn() {
        fontSize = Math.min(fontSize + 2, 72);
        textArea.setFont(new Font("Consolas", Font.PLAIN, fontSize));
    }
    
    public void zoomOut() {
        fontSize = Math.max(fontSize - 2, 8);
        textArea.setFont(new Font("Consolas", Font.PLAIN, fontSize));
    }
    
    public void resetZoom() {
        fontSize = 14;
        textArea.setFont(new Font("Consolas", Font.PLAIN, fontSize));
    }
    
    public String getFilePath() { return filePath; }
    public boolean isModified() { return modified; }
    public JTextArea getTextArea() { return textArea; }
    
    /**
     * 查找文本
     */
    public boolean find(String text) {
        if (text == null || text.isEmpty()) return false;
        String content = textArea.getText();
        int pos = content.indexOf(text, textArea.getCaretPosition());
        if (pos < 0) {
            pos = content.indexOf(text);
        }
        if (pos >= 0) {
            textArea.setCaretPosition(pos);
            textArea.select(pos, pos + text.length());
            return true;
        }
        return false;
    }
    
    /**
     * 替换文本
     */
    public boolean replace(String oldText, String newText) {
        if (oldText == null || oldText.isEmpty()) return false;
        String selected = textArea.getSelectedText();
        if (selected != null && selected.equals(oldText)) {
            textArea.replaceSelection(newText);
            return true;
        }
        if (find(oldText)) {
            textArea.replaceSelection(newText);
            return true;
        }
        return false;
    }
}
