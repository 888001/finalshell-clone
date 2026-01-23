package com.finalshell.hotkey;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

/**
 * 快捷键工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class HotkeyTools {
    
    public static String keyStrokeToString(KeyStroke ks) {
        if (ks == null) return "";
        
        StringBuilder sb = new StringBuilder();
        int modifiers = ks.getModifiers();
        
        if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
            sb.append("Ctrl+");
        }
        if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
            sb.append("Alt+");
        }
        if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
            sb.append("Shift+");
        }
        if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
            sb.append("Meta+");
        }
        
        sb.append(KeyEvent.getKeyText(ks.getKeyCode()));
        return sb.toString();
    }
    
    public static KeyStroke stringToKeyStroke(String str) {
        if (str == null || str.isEmpty()) return null;
        
        int modifiers = 0;
        String keyPart = str;
        
        if (str.contains("Ctrl+")) {
            modifiers |= InputEvent.CTRL_DOWN_MASK;
            keyPart = keyPart.replace("Ctrl+", "");
        }
        if (str.contains("Alt+")) {
            modifiers |= InputEvent.ALT_DOWN_MASK;
            keyPart = keyPart.replace("Alt+", "");
        }
        if (str.contains("Shift+")) {
            modifiers |= InputEvent.SHIFT_DOWN_MASK;
            keyPart = keyPart.replace("Shift+", "");
        }
        if (str.contains("Meta+")) {
            modifiers |= InputEvent.META_DOWN_MASK;
            keyPart = keyPart.replace("Meta+", "");
        }
        
        int keyCode = getKeyCode(keyPart);
        if (keyCode != KeyEvent.VK_UNDEFINED) {
            return KeyStroke.getKeyStroke(keyCode, modifiers);
        }
        return null;
    }
    
    private static int getKeyCode(String keyText) {
        if (keyText.length() == 1) {
            char c = keyText.toUpperCase().charAt(0);
            if (c >= 'A' && c <= 'Z') {
                return KeyEvent.VK_A + (c - 'A');
            }
            if (c >= '0' && c <= '9') {
                return KeyEvent.VK_0 + (c - '0');
            }
        }
        
        switch (keyText.toUpperCase()) {
            case "F1": return KeyEvent.VK_F1;
            case "F2": return KeyEvent.VK_F2;
            case "F3": return KeyEvent.VK_F3;
            case "F4": return KeyEvent.VK_F4;
            case "F5": return KeyEvent.VK_F5;
            case "F6": return KeyEvent.VK_F6;
            case "F7": return KeyEvent.VK_F7;
            case "F8": return KeyEvent.VK_F8;
            case "F9": return KeyEvent.VK_F9;
            case "F10": return KeyEvent.VK_F10;
            case "F11": return KeyEvent.VK_F11;
            case "F12": return KeyEvent.VK_F12;
            case "ENTER": return KeyEvent.VK_ENTER;
            case "ESCAPE": return KeyEvent.VK_ESCAPE;
            case "TAB": return KeyEvent.VK_TAB;
            case "SPACE": return KeyEvent.VK_SPACE;
            case "DELETE": return KeyEvent.VK_DELETE;
            case "INSERT": return KeyEvent.VK_INSERT;
            case "HOME": return KeyEvent.VK_HOME;
            case "END": return KeyEvent.VK_END;
            default: return KeyEvent.VK_UNDEFINED;
        }
    }
    
    public static boolean isValidKeyStroke(KeyStroke ks) {
        return ks != null && ks.getKeyCode() != KeyEvent.VK_UNDEFINED;
    }
}
