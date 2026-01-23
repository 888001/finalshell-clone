package com.finalshell.terminal;

import javax.swing.*;
import java.awt.event.*;

/**
 * 命令选项菜单
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CmdOptionMenu extends JPopupMenu {
    
    private JMenuItem copyItem;
    private JMenuItem pasteItem;
    private JMenuItem selectAllItem;
    private JMenuItem clearItem;
    private JMenuItem historyItem;
    private JMenuItem helpItem;
    private MenuListener listener;
    
    public CmdOptionMenu() {
        initUI();
    }
    
    private void initUI() {
        copyItem = new JMenuItem("复制");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> {
            if (listener != null) listener.onCopy();
        });
        
        pasteItem = new JMenuItem("粘贴");
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> {
            if (listener != null) listener.onPaste();
        });
        
        selectAllItem = new JMenuItem("全选");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllItem.addActionListener(e -> {
            if (listener != null) listener.onSelectAll();
        });
        
        clearItem = new JMenuItem("清屏");
        clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        clearItem.addActionListener(e -> {
            if (listener != null) listener.onClear();
        });
        
        historyItem = new JMenuItem("历史命令");
        historyItem.addActionListener(e -> {
            if (listener != null) listener.onHistory();
        });
        
        helpItem = new JMenuItem("帮助");
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpItem.addActionListener(e -> {
            if (listener != null) listener.onHelp();
        });
        
        add(copyItem);
        add(pasteItem);
        add(selectAllItem);
        addSeparator();
        add(clearItem);
        add(historyItem);
        addSeparator();
        add(helpItem);
    }
    
    public void setMenuListener(MenuListener listener) {
        this.listener = listener;
    }
    
    public interface MenuListener {
        void onCopy();
        void onPaste();
        void onSelectAll();
        void onClear();
        void onHistory();
        void onHelp();
    }
}
