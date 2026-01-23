package com.finalshell.command;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import java.awt.event.*;

/**
 * 命令输入文本框
 * 支持历史记录和自动完成
 */
public class CmdTextField extends JTextField {
    
    private UndoManager undoManager;
    private RecentCmdList recentCmdList;
    private AutoPopupList autoPopupList;
    
    public CmdTextField() {
        this(20);
    }
    
    public CmdTextField(int columns) {
        super(columns);
        initUndoRedo();
        initRecentCommands();
    }
    
    private void initUndoRedo() {
        undoManager = new UndoManager();
        getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
        
        getInputMap().put(KeyStroke.getKeyStroke("control Z"), "undo");
        getInputMap().put(KeyStroke.getKeyStroke("control Y"), "redo");
        
        getActionMap().put("undo", new UndoAction(undoManager));
        getActionMap().put("redo", new RedoAction(undoManager));
    }
    
    private void initRecentCommands() {
        recentCmdList = new RecentCmdList();
        autoPopupList = new AutoPopupList(this);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP && e.isControlDown()) {
                    autoPopupList.showPopup();
                }
            }
        });
    }
    
    public void addToHistory(String command) {
        recentCmdList.add(command);
        autoPopupList.addItem(command);
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
    
    public RecentCmdList getRecentCmdList() { return recentCmdList; }
    public AutoPopupList getAutoPopupList() { return autoPopupList; }
}
