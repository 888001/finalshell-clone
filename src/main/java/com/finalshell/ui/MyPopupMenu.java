package com.finalshell.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 自定义弹出菜单
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - MyPopupMenu
 */
public class MyPopupMenu extends JPopupMenu {
    
    private JMenuItem copyItem;
    private JMenuItem cutItem;
    private JMenuItem pasteItem;
    private JMenuItem selectAllItem;
    private JMenuItem deleteItem;
    
    private JTextComponent targetComponent;
    
    public MyPopupMenu() {
        initMenuItems();
    }
    
    public MyPopupMenu(JTextComponent target) {
        this.targetComponent = target;
        initMenuItems();
        bindToComponent(target);
    }
    
    private void initMenuItems() {
        copyItem = new JMenuItem("复制");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> doCopy());
        
        cutItem = new JMenuItem("剪切");
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(e -> doCut());
        
        pasteItem = new JMenuItem("粘贴");
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> doPaste());
        
        selectAllItem = new JMenuItem("全选");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllItem.addActionListener(e -> doSelectAll());
        
        deleteItem = new JMenuItem("删除");
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteItem.addActionListener(e -> doDelete());
        
        add(cutItem);
        add(copyItem);
        add(pasteItem);
        add(deleteItem);
        addSeparator();
        add(selectAllItem);
    }
    
    public void bindToComponent(JTextComponent component) {
        this.targetComponent = component;
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupIfNeeded(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupIfNeeded(e);
            }
            private void showPopupIfNeeded(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    updateMenuState();
                    show(component, e.getX(), e.getY());
                }
            }
        });
    }
    
    private void updateMenuState() {
        if (targetComponent != null) {
            boolean hasSelection = targetComponent.getSelectedText() != null;
            boolean editable = targetComponent.isEditable();
            
            copyItem.setEnabled(hasSelection);
            cutItem.setEnabled(hasSelection && editable);
            deleteItem.setEnabled(hasSelection && editable);
            pasteItem.setEnabled(editable);
        }
    }
    
    private void doCopy() {
        if (targetComponent != null) {
            targetComponent.copy();
        }
    }
    
    private void doCut() {
        if (targetComponent != null) {
            targetComponent.cut();
        }
    }
    
    private void doPaste() {
        if (targetComponent != null) {
            targetComponent.paste();
        }
    }
    
    private void doSelectAll() {
        if (targetComponent != null) {
            targetComponent.selectAll();
        }
    }
    
    private void doDelete() {
        if (targetComponent != null && targetComponent.isEditable()) {
            try {
                Document doc = targetComponent.getDocument();
                int start = targetComponent.getSelectionStart();
                int end = targetComponent.getSelectionEnd();
                if (start != end) {
                    doc.remove(start, end - start);
                }
            } catch (BadLocationException e) {
                // ignore
            }
        }
    }
}
