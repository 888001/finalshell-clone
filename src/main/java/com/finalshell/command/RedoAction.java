package com.finalshell.command;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;

/**
 * 重做操作
 */
public class RedoAction extends AbstractAction {
    
    private UndoManager undoManager;
    
    public RedoAction(UndoManager undoManager) {
        super("重做");
        this.undoManager = undoManager;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }
}
