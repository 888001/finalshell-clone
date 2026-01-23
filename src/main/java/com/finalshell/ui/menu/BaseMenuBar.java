package com.finalshell.ui.menu;

import javax.swing.*;
import java.awt.event.*;

/**
 * 基础菜单栏
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Font_Menu_FullScreen_UI_DeepAnalysis.md - BaseMenuBar
 */
public class BaseMenuBar extends JMenuBar {
    
    protected JMenu createMenu(String text) {
        return new JMenu(text);
    }
    
    protected JMenuItem createMenuItem(String text, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        if (action != null) {
            item.addActionListener(action);
        }
        return item;
    }
    
    protected JMenuItem createMenuItem(String text, int key, int modifiers, ActionListener action) {
        JMenuItem item = createMenuItem(text, action);
        if (key != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));
        }
        return item;
    }
    
    protected JCheckBoxMenuItem createCheckMenuItem(String text, boolean selected, ActionListener action) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(text, selected);
        if (action != null) {
            item.addActionListener(action);
        }
        return item;
    }
    
    protected JRadioButtonMenuItem createRadioMenuItem(String text, ButtonGroup group, ActionListener action) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(text);
        if (group != null) {
            group.add(item);
        }
        if (action != null) {
            item.addActionListener(action);
        }
        return item;
    }
}
