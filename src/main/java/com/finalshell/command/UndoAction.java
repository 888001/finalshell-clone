package com.finalshell.command;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;

/**
 * 撤销操作
 */
public class UndoAction extends AbstractAction {
    
    private UndoManager undoManager;
    
    public UndoAction(UndoManager undoManager) {
        super("撤销");
        this.undoManager = undoManager;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }
}
