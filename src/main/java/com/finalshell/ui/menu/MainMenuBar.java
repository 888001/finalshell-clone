package com.finalshell.ui.menu;

import com.finalshell.ui.MainWindow;

import javax.swing.*;
import java.awt.event.*;

/**
 * 主窗口菜单栏
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Font_Menu_FullScreen_UI_DeepAnalysis.md - MenuBar
 */
public class MainMenuBar extends BaseMenuBar {
    
    private MainWindow mainWindow;
    
    public MainMenuBar(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initMenus();
    }
    
    private void initMenus() {
        // 文件菜单
        JMenu fileMenu = createMenu("文件");
        fileMenu.add(createMenuItem("新建连接", KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK, 
            e -> mainWindow.showNewConnectionDialog()));
        fileMenu.add(createMenuItem("打开连接", KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK, 
            e -> {}));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("导入配置", null));
        fileMenu.add(createMenuItem("导出配置", null));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("退出", KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK, 
            e -> mainWindow.dispose()));
        add(fileMenu);
        
        // 编辑菜单
        JMenu editMenu = createMenu("编辑");
        editMenu.add(createMenuItem("复制", KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK, null));
        editMenu.add(createMenuItem("粘贴", KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK, null));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("查找", KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK, null));
        add(editMenu);
        
        // 视图菜单
        JMenu viewMenu = createMenu("视图");
        viewMenu.add(createMenuItem("全屏", KeyEvent.VK_F11, 0, 
            e -> mainWindow.toggleFullScreen()));
        viewMenu.addSeparator();
        viewMenu.add(createMenuItem("放大", KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK, null));
        viewMenu.add(createMenuItem("缩小", KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK, null));
        viewMenu.add(createMenuItem("重置缩放", KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK, null));
        add(viewMenu);
        
        // 工具菜单
        JMenu toolsMenu = createMenu("工具");
        toolsMenu.add(createMenuItem("端口转发", null));
        toolsMenu.add(createMenuItem("SSH密钥管理", null));
        toolsMenu.addSeparator();
        toolsMenu.add(createMenuItem("网络诊断", null));
        toolsMenu.add(createMenuItem("系统监控", null));
        add(toolsMenu);
        
        // 设置菜单
        JMenu settingsMenu = createMenu("设置");
        settingsMenu.add(createMenuItem("偏好设置", KeyEvent.VK_COMMA, InputEvent.CTRL_DOWN_MASK, 
            e -> mainWindow.showSettingsDialog()));
        settingsMenu.add(createMenuItem("字体设置", null));
        settingsMenu.add(createMenuItem("主题设置", null));
        add(settingsMenu);
        
        // 帮助菜单
        JMenu helpMenu = createMenu("帮助");
        helpMenu.add(createMenuItem("帮助文档", KeyEvent.VK_F1, 0, null));
        helpMenu.addSeparator();
        helpMenu.add(createMenuItem("检查更新", null));
        helpMenu.add(createMenuItem("关于", null));
        add(helpMenu);
    }
}
