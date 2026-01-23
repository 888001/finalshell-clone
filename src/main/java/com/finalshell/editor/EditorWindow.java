package com.finalshell.editor;

import com.finalshell.ui.TabBar;
import com.finalshell.ui.TabWrap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

/**
 * 编辑器窗口
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Editor_Event_DeepAnalysis.md - EditorWindow
 */
public class EditorWindow extends JFrame {
    
    private static final long serialVersionUID = -4978681285982446796L;
    
    private TabBar tabBar;
    private JPanel contentPane;
    private EditorPanel currentEditorPanel;
    private JLabel statusLabel;
    
    public EditorWindow() {
        super("文件编辑器");
        initComponents();
        initMenuBar();
        initListeners();
        
        setSize(900, 700);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(false);
        setContentPane(contentPane);
        
        // 标签栏
        tabBar = new TabBar();
        contentPane.add(tabBar, BorderLayout.NORTH);
        
        // 状态栏
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel(" 就绪");
        statusBar.add(statusLabel, BorderLayout.WEST);
        contentPane.add(statusBar, BorderLayout.SOUTH);
    }
    
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        fileMenu.add(createMenuItem("新建", KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK, e -> newFile()));
        fileMenu.add(createMenuItem("打开", KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK, e -> openFile()));
        fileMenu.add(createMenuItem("保存", KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, e -> saveFile()));
        fileMenu.add(createMenuItem("另存为...", 0, 0, e -> saveFileAs()));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("关闭", KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK, e -> closeCurrentTab()));
        menuBar.add(fileMenu);
        
        // 编辑菜单
        JMenu editMenu = new JMenu("编辑");
        editMenu.add(createMenuItem("撤销", KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK, e -> undo()));
        editMenu.add(createMenuItem("重做", KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK, e -> redo()));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("剪切", KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK, e -> cut()));
        editMenu.add(createMenuItem("复制", KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK, e -> copy()));
        editMenu.add(createMenuItem("粘贴", KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK, e -> paste()));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("查找", KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK, e -> showFindDialog()));
        editMenu.add(createMenuItem("替换", KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK, e -> showReplaceDialog()));
        menuBar.add(editMenu);
        
        // 视图菜单
        JMenu viewMenu = new JMenu("视图");
        viewMenu.add(createMenuItem("放大字体", KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK, e -> zoomIn()));
        viewMenu.add(createMenuItem("缩小字体", KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK, e -> zoomOut()));
        viewMenu.add(createMenuItem("重置字体", KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK, e -> resetZoom()));
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JMenuItem createMenuItem(String text, int key, int modifiers, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        if (key != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));
        }
        item.addActionListener(action);
        return item;
    }
    
    private void initListeners() {
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (currentEditorPanel != null) {
                    currentEditorPanel.checkFileModified();
                }
            }
            
            @Override
            public void windowLostFocus(WindowEvent e) {}
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });
    }
    
    /**
     * 打开文件
     */
    public void openFile(String path) {
        // 检查是否已打开
        for (EditorPanel panel : getOpenedPanels()) {
            if (path.equals(panel.getFilePath())) {
                selectPanel(panel);
                return;
            }
        }
        
        // 创建新标签
        EditorPanel panel = new EditorPanel();
        panel.loadFile(path);
        
        File file = new File(path);
        tabBar.addTab(file.getName(), panel);
        currentEditorPanel = panel;
        
        updateStatus("已打开: " + path);
    }
    
    private java.util.List<EditorPanel> getOpenedPanels() {
        java.util.List<EditorPanel> panels = new ArrayList<>();
        for (TabWrap wrap : tabBar.getTabList()) {
            if (wrap.getComponent() instanceof EditorPanel) {
                panels.add((EditorPanel) wrap.getComponent());
            }
        }
        return panels;
    }
    
    private void selectPanel(EditorPanel panel) {
        for (TabWrap wrap : tabBar.getTabList()) {
            if (wrap.getComponent() == panel) {
                tabBar.selectTab(wrap);
                currentEditorPanel = panel;
                break;
            }
        }
    }
    
    public void closeWindow() {
        setVisible(false);
        tabBar.closeAllTabs();
    }
    
    public void refreshAllTabs() {
        for (EditorPanel panel : getOpenedPanels()) {
            panel.checkFileModified();
        }
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(" " + message);
    }
    
    // 菜单动作
    private void newFile() {
        EditorPanel panel = new EditorPanel();
        tabBar.addTab("未命名", panel);
        currentEditorPanel = panel;
    }
    
    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            openFile(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void saveFile() {
        if (currentEditorPanel != null) {
            currentEditorPanel.save();
            updateStatus("已保存");
        }
    }
    
    private void saveFileAs() {
        if (currentEditorPanel != null) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentEditorPanel.saveAs(chooser.getSelectedFile().getAbsolutePath());
                updateStatus("已另存为: " + chooser.getSelectedFile().getName());
            }
        }
    }
    
    private void closeCurrentTab() {
        if (currentEditorPanel != null) {
            for (TabWrap wrap : tabBar.getTabList()) {
                if (wrap.getComponent() == currentEditorPanel) {
                    tabBar.closeTab(wrap);
                    break;
                }
            }
        }
    }
    
    private void undo() {
        if (currentEditorPanel != null) currentEditorPanel.undo();
    }
    
    private void redo() {
        if (currentEditorPanel != null) currentEditorPanel.redo();
    }
    
    private void cut() {
        if (currentEditorPanel != null) currentEditorPanel.cut();
    }
    
    private void copy() {
        if (currentEditorPanel != null) currentEditorPanel.copy();
    }
    
    private void paste() {
        if (currentEditorPanel != null) currentEditorPanel.paste();
    }
    
    private void showFindDialog() {
        // TODO: 实现查找对话框
    }
    
    private void showReplaceDialog() {
        // TODO: 实现替换对话框
    }
    
    private void zoomIn() {
        if (currentEditorPanel != null) currentEditorPanel.zoomIn();
    }
    
    private void zoomOut() {
        if (currentEditorPanel != null) currentEditorPanel.zoomOut();
    }
    
    private void resetZoom() {
        if (currentEditorPanel != null) currentEditorPanel.resetZoom();
    }
}
