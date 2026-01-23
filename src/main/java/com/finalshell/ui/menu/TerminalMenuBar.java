package com.finalshell.ui.menu;

import javax.swing.*;
import java.awt.event.*;

/**
 * 终端菜单栏
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Font_Menu_FullScreen_UI_DeepAnalysis.md - TerminalMenuBar
 */
public class TerminalMenuBar extends BaseMenuBar {
    
    private TerminalMenuListener listener;
    
    public TerminalMenuBar() {
        initMenus();
    }
    
    private void initMenus() {
        // 终端菜单
        JMenu termMenu = createMenu("终端");
        termMenu.add(createMenuItem("新建标签", KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK, 
            e -> { if (listener != null) listener.onNewTab(); }));
        termMenu.add(createMenuItem("关闭标签", KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK, 
            e -> { if (listener != null) listener.onCloseTab(); }));
        termMenu.addSeparator();
        termMenu.add(createMenuItem("重新连接", KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK, 
            e -> { if (listener != null) listener.onReconnect(); }));
        termMenu.add(createMenuItem("断开连接", null));
        add(termMenu);
        
        // 编辑菜单
        JMenu editMenu = createMenu("编辑");
        editMenu.add(createMenuItem("复制", KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, 
            e -> { if (listener != null) listener.onCopy(); }));
        editMenu.add(createMenuItem("粘贴", KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, 
            e -> { if (listener != null) listener.onPaste(); }));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("清屏", KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK, 
            e -> { if (listener != null) listener.onClear(); }));
        add(editMenu);
        
        // 视图菜单
        JMenu viewMenu = createMenu("视图");
        viewMenu.add(createMenuItem("全屏", KeyEvent.VK_F11, 0, 
            e -> { if (listener != null) listener.onFullScreen(); }));
        viewMenu.addSeparator();
        viewMenu.add(createMenuItem("放大", null));
        viewMenu.add(createMenuItem("缩小", null));
        add(viewMenu);
    }
    
    public void setListener(TerminalMenuListener listener) {
        this.listener = listener;
    }
    
    public interface TerminalMenuListener {
        void onNewTab();
        void onCloseTab();
        void onReconnect();
        void onCopy();
        void onPaste();
        void onClear();
        void onFullScreen();
    }
}
