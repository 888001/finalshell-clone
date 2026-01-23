package com.finalshell.hotkey;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

/**
 * 应用级键盘监听器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AppKeyListener implements KeyEventDispatcher {
    
    private static AppKeyListener instance;
    private HotkeyManager hotkeyManager;
    private boolean enabled = true;
    
    private AppKeyListener() {
        this.hotkeyManager = HotkeyManager.getInstance();
    }
    
    public static synchronized AppKeyListener getInstance() {
        if (instance == null) {
            instance = new AppKeyListener();
        }
        return instance;
    }
    
    public void install() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(this);
    }
    
    public void uninstall() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .removeKeyEventDispatcher(this);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (!enabled) {
            return false;
        }
        
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            return handleKeyPressed(e);
        }
        return false;
    }
    
    private boolean handleKeyPressed(KeyEvent e) {
        if (hotkeyManager != null) {
            return hotkeyManager.processKeyEvent(e);
        }
        return false;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setHotkeyManager(HotkeyManager manager) {
        this.hotkeyManager = manager;
    }
}
