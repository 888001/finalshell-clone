package com.finalshell.editor;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 内置文本编辑器
 */
public class TextEditor extends JPanel {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    private JComboBox<String> encodingCombo;
    
    private UndoManager undoManager;
    private String filePath;
    private Charset charset = StandardCharsets.UTF_8;
    private boolean modified = false;
    
    private EditorCallback callback;
    
    public TextEditor() {
        this(null);
    }
    
    public TextEditor(EditorCallback callback) {
        this.callback = callback;
        setLayout(new BorderLayout());
        initComponents();
        setupKeyBindings();
    }
    
    private void initComponents() {
        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton saveBtn = new JButton("保存");
        JButton undoBtn = new JButton("撤销");
        JButton redoBtn = new JButton("重做");
        JButton findBtn = new JButton("查找");
        JButton gotoBtn = new JButton("跳转");
        
        saveBtn.addActionListener(e -> save());
        undoBtn.addActionListener(e -> undo());
        redoBtn.addActionListener(e -> redo());
        findBtn.addActionListener(e -> showFindDialog());
        gotoBtn.addActionListener(e -> showGotoDialog());
        
        toolBar.add(saveBtn);
        toolBar.addSeparator();
        toolBar.add(undoBtn);
        toolBar.add(redoBtn);
        toolBar.addSeparator();
        toolBar.add(findBtn);
        toolBar.add(gotoBtn);
        toolBar.addSeparator();
        toolBar.add(new JLabel(" 编码: "));
        encodingCombo = new JComboBox<>(new String[]{"UTF-8", "GBK", "GB2312", "ISO-8859-1", "ASCII"});
        encodingCombo.addActionListener(e -> reloadWithEncoding());
        toolBar.add(encodingCombo);
        
        add(toolBar, BorderLayout.NORTH);
        
        // 文本区域
        textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textArea.setTabSize(4);
        
        // 行号
        JTextArea lineNumbers = new LineNumberArea(textArea);
        
        scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);
        add(scrollPane, BorderLayout.CENTER);
        
        // 状态栏
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusLabel = new JLabel("就绪");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        // 撤销管理
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(e -> {
            undoManager.addEdit(e.getEdit());
        });
        
        // 修改监听
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { setModified(true); }
            @Override
            public void removeUpdate(DocumentEvent e) { setModified(true); }
            @Override
            public void changedUpdate(DocumentEvent e) { setModified(true); }
        });
        
        // 光标位置更新
        textArea.addCaretListener(e -> updateStatus());
    }
    
    private void setupKeyBindings() {
        InputMap im = textArea.getInputMap(WHEN_FOCUSED);
        ActionMap am = textArea.getActionMap();
        
        // Ctrl+S 保存
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        am.put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { save(); }
        });
        
        // Ctrl+Z 撤销
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        am.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { undo(); }
        });
        
        // Ctrl+Y 重做
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        am.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { redo(); }
        });
        
        // Ctrl+F 查找
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "find");
        am.put("find", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { showFindDialog(); }
        });
        
        // Ctrl+G 跳转
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), "goto");
        am.put("goto", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { showGotoDialog(); }
        });
    }
    
    public void setText(String text) {
        textArea.setText(text);
        textArea.setCaretPosition(0);
        undoManager.discardAllEdits();
        setModified(false);
    }
    
    public String getText() {
        return textArea.getText();
    }
    
    public void setFilePath(String path) {
        this.filePath = path;
        updateStatus();
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void loadFromString(String content, String path) {
        this.filePath = path;
        setText(content);
    }
    
    private void save() {
        if (callback != null) {
            callback.onSave(filePath, getText(), charset);
        }
        setModified(false);
    }
    
    private void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }
    
    private void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }
    
    private void showFindDialog() {
        String search = JOptionPane.showInputDialog(this, "查找内容:", "查找", JOptionPane.PLAIN_MESSAGE);
        if (search == null || search.isEmpty()) return;
        
        String text = textArea.getText();
        int pos = text.indexOf(search, textArea.getCaretPosition());
        if (pos < 0) {
            pos = text.indexOf(search); // 从头搜索
        }
        
        if (pos >= 0) {
            textArea.setCaretPosition(pos);
            textArea.select(pos, pos + search.length());
            textArea.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "未找到: " + search, "查找", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showGotoDialog() {
        String input = JOptionPane.showInputDialog(this, "跳转到行号:", "跳转", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.isEmpty()) return;
        
        try {
            int line = Integer.parseInt(input.trim()) - 1;
            if (line < 0) line = 0;
            
            int pos = textArea.getLineStartOffset(line);
            textArea.setCaretPosition(pos);
            textArea.requestFocus();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "无效的行号", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(this, "行号超出范围", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reloadWithEncoding() {
        String selected = (String) encodingCombo.getSelectedItem();
        if (selected != null) {
            charset = Charset.forName(selected);
            if (callback != null && filePath != null) {
                callback.onReload(filePath, charset);
            }
        }
    }
    
    private void setModified(boolean modified) {
        this.modified = modified;
        updateStatus();
    }
    
    private void updateStatus() {
        try {
            int pos = textArea.getCaretPosition();
            int line = textArea.getLineOfOffset(pos) + 1;
            int col = pos - textArea.getLineStartOffset(line - 1) + 1;
            
            String modStr = modified ? " [已修改]" : "";
            String fileName = filePath != null ? filePath : "未命名";
            statusLabel.setText(String.format("%s%s | 行 %d, 列 %d | %s", 
                fileName, modStr, line, col, charset.name()));
        } catch (BadLocationException e) {
            statusLabel.setText(filePath != null ? filePath : "未命名");
        }
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }
    
    /**
     * 编辑器回调
     */
    public interface EditorCallback {
        void onSave(String path, String content, Charset charset);
        void onReload(String path, Charset charset);
    }
    
    /**
     * 行号组件
     */
    private static class LineNumberArea extends JTextArea {
        private final JTextArea textArea;
        
        public LineNumberArea(JTextArea textArea) {
            this.textArea = textArea;
            setEditable(false);
            setFont(textArea.getFont());
            setBackground(new Color(240, 240, 240));
            setForeground(Color.GRAY);
            
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { updateLineNumbers(); }
                @Override
                public void removeUpdate(DocumentEvent e) { updateLineNumbers(); }
                @Override
                public void changedUpdate(DocumentEvent e) { updateLineNumbers(); }
            });
            
            updateLineNumbers();
        }
        
        private void updateLineNumbers() {
            int lines = textArea.getLineCount();
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= lines; i++) {
                sb.append(String.format("%4d%n", i));
            }
            setText(sb.toString());
        }
    }
}
