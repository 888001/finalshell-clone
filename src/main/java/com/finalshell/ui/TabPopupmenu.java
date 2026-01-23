package com.finalshell.ui;

import javax.swing.*;
import java.awt.event.*;

/**
 * 标签页右键菜单
 * 提供标签页的右键操作菜单
 */
public class TabPopupmenu extends JPopupMenu {
    
    private JTabbedPane tabbedPane;
    private int tabIndex = -1;
    
    private JMenuItem closeItem;
    private JMenuItem closeOthersItem;
    private JMenuItem closeAllItem;
    private JMenuItem closeLeftItem;
    private JMenuItem closeRightItem;
    private JMenuItem duplicateItem;
    private JMenuItem renameItem;
    
    public TabPopupmenu(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        initMenu();
    }
    
    private void initMenu() {
        closeItem = new JMenuItem("关闭");
        closeItem.addActionListener(e -> closeTab());
        add(closeItem);
        
        closeOthersItem = new JMenuItem("关闭其他");
        closeOthersItem.addActionListener(e -> closeOtherTabs());
        add(closeOthersItem);
        
        closeAllItem = new JMenuItem("关闭全部");
        closeAllItem.addActionListener(e -> closeAllTabs());
        add(closeAllItem);
        
        addSeparator();
        
        closeLeftItem = new JMenuItem("关闭左侧");
        closeLeftItem.addActionListener(e -> closeLeftTabs());
        add(closeLeftItem);
        
        closeRightItem = new JMenuItem("关闭右侧");
        closeRightItem.addActionListener(e -> closeRightTabs());
        add(closeRightItem);
        
        addSeparator();
        
        duplicateItem = new JMenuItem("复制标签");
        duplicateItem.addActionListener(e -> duplicateTab());
        add(duplicateItem);
        
        renameItem = new JMenuItem("重命名");
        renameItem.addActionListener(e -> renameTab());
        add(renameItem);
    }
    
    public void show(java.awt.Component invoker, int x, int y, int tabIndex) {
        this.tabIndex = tabIndex;
        updateMenuState();
        super.show(invoker, x, y);
    }
    
    private void updateMenuState() {
        int tabCount = tabbedPane.getTabCount();
        closeItem.setEnabled(tabIndex >= 0);
        closeOthersItem.setEnabled(tabCount > 1);
        closeAllItem.setEnabled(tabCount > 0);
        closeLeftItem.setEnabled(tabIndex > 0);
        closeRightItem.setEnabled(tabIndex < tabCount - 1);
    }
    
    private void closeTab() {
        if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
            tabbedPane.removeTabAt(tabIndex);
        }
    }
    
    private void closeOtherTabs() {
        if (tabIndex < 0) return;
        for (int i = tabbedPane.getTabCount() - 1; i >= 0; i--) {
            if (i != tabIndex) {
                tabbedPane.removeTabAt(i);
            }
        }
    }
    
    private void closeAllTabs() {
        tabbedPane.removeAll();
    }
    
    private void closeLeftTabs() {
        for (int i = tabIndex - 1; i >= 0; i--) {
            tabbedPane.removeTabAt(i);
        }
    }
    
    private void closeRightTabs() {
        for (int i = tabbedPane.getTabCount() - 1; i > tabIndex; i--) {
            tabbedPane.removeTabAt(i);
        }
    }
    
    private void duplicateTab() {}
    private void renameTab() {}
}
