package com.finalshell.ui.menu;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * 自定义菜单项
 * 扩展JMenuItem提供额外功能
 */
public class MyMenuItem extends JMenuItem {
    
    private static final long serialVersionUID = 1L;
    
    private String actionCommand;
    private Object userData;
    
    public MyMenuItem() {
        super();
    }
    
    public MyMenuItem(String text) {
        super(text);
    }
    
    public MyMenuItem(String text, Icon icon) {
        super(text, icon);
    }
    
    public MyMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
    }
    
    public MyMenuItem(String text, ActionListener listener) {
        super(text);
        addActionListener(listener);
    }
    
    public MyMenuItem(String text, Icon icon, ActionListener listener) {
        super(text, icon);
        addActionListener(listener);
    }
    
    public void setUserData(Object data) {
        this.userData = data;
    }
    
    public Object getUserData() {
        return userData;
    }
    
    @Override
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
        super.setActionCommand(actionCommand);
    }
    
    @Override
    public String getActionCommand() {
        return actionCommand != null ? actionCommand : super.getActionCommand();
    }
}
