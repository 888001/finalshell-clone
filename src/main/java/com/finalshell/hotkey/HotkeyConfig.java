package com.finalshell.hotkey;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * 热键配置
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Hotkey_Management_DeepAnalysis.md - HotkeyConfig
 */
public class HotkeyConfig {
    
    private int id;
    private String name;
    private String actionName;
    private int modifiers;
    private int keyCode;
    private KeyStroke keyStroke;
    
    public HotkeyConfig() {}
    
    public HotkeyConfig(int id, String name, int modifiers, int keyCode) {
        this.id = id;
        this.name = name;
        this.modifiers = modifiers;
        this.keyCode = keyCode;
        updateKeyStroke();
    }
    
    /**
     * 更新KeyStroke
     */
    public void updateKeyStroke() {
        this.keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
    }
    
    /**
     * 获取热键描述
     */
    public String getKeyText() {
        StringBuilder sb = new StringBuilder();
        
        if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) {
            sb.append("Ctrl+");
        }
        if ((modifiers & KeyEvent.ALT_DOWN_MASK) != 0) {
            sb.append("Alt+");
        }
        if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0) {
            sb.append("Shift+");
        }
        if ((modifiers & KeyEvent.META_DOWN_MASK) != 0) {
            sb.append("Meta+");
        }
        
        sb.append(KeyEvent.getKeyText(keyCode));
        return sb.toString();
    }
    
    /**
     * 从KeyEvent设置
     */
    public void setFromKeyEvent(KeyEvent e) {
        this.modifiers = e.getModifiersEx();
        this.keyCode = e.getKeyCode();
        updateKeyStroke();
    }
    
    /**
     * 匹配KeyEvent
     */
    public boolean matches(KeyEvent e) {
        return keyCode == e.getKeyCode() && 
               (modifiers & e.getModifiersEx()) == modifiers;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getModifiers() { return modifiers; }
    public void setModifiers(int modifiers) { 
        this.modifiers = modifiers;
        updateKeyStroke();
    }
    
    public int getKeyCode() { return keyCode; }
    public void setKeyCode(int keyCode) { 
        this.keyCode = keyCode;
        updateKeyStroke();
    }
    
    public KeyStroke getKeyStroke() { return keyStroke; }
    public void setKeyStroke(KeyStroke keyStroke) { this.keyStroke = keyStroke; }
    
    public String getActionName() { return actionName != null ? actionName : name; }
    public void setActionName(String actionName) { this.actionName = actionName; }
    
    public String getKeyStrokeString() { return getKeyText(); }
    
    public boolean isCtrl() { return (modifiers & KeyEvent.CTRL_DOWN_MASK) != 0; }
    public void setCtrl(boolean ctrl) {
        if (ctrl) modifiers |= KeyEvent.CTRL_DOWN_MASK;
        else modifiers &= ~KeyEvent.CTRL_DOWN_MASK;
        updateKeyStroke();
    }
    
    public boolean isAlt() { return (modifiers & KeyEvent.ALT_DOWN_MASK) != 0; }
    public void setAlt(boolean alt) {
        if (alt) modifiers |= KeyEvent.ALT_DOWN_MASK;
        else modifiers &= ~KeyEvent.ALT_DOWN_MASK;
        updateKeyStroke();
    }
    
    public boolean isShift() { return (modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0; }
    public void setShift(boolean shift) {
        if (shift) modifiers |= KeyEvent.SHIFT_DOWN_MASK;
        else modifiers &= ~KeyEvent.SHIFT_DOWN_MASK;
        updateKeyStroke();
    }
    
    @Override
    public String toString() {
        return name + " (" + getKeyText() + ")";
    }
}
