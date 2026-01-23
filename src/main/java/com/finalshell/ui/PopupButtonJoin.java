package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 按钮与弹出菜单关联
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - PopupButtonJoin
 */
public class PopupButtonJoin {
    
    private JButton button;
    private JPopupMenu popupMenu;
    private List<PopupItem> items;
    
    public PopupButtonJoin(JButton button, List<PopupItem> items) {
        this.button = button;
        this.items = items;
        
        initPopupMenu();
        attachButton();
    }
    
    private void initPopupMenu() {
        popupMenu = new JPopupMenu();
        
        for (PopupItem item : items) {
            if (item.isSeparator()) {
                popupMenu.addSeparator();
            } else {
                JMenuItem menuItem = new JMenuItem(item.getText());
                menuItem.setIcon(item.getIcon());
                menuItem.setEnabled(item.isEnabled());
                menuItem.setActionCommand(item.getCommand());
                popupMenu.add(menuItem);
            }
        }
    }
    
    private void attachButton() {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup();
            }
        });
    }
    
    public void showPopup() {
        popupMenu.show(button, 0, button.getHeight());
    }
    
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }
    
    public void addActionListener(ActionListener listener) {
        for (int i = 0; i < popupMenu.getComponentCount(); i++) {
            Component comp = popupMenu.getComponent(i);
            if (comp instanceof JMenuItem) {
                ((JMenuItem) comp).addActionListener(listener);
            }
        }
    }
}
