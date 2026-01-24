package com.finalshell.hotkey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 热键管理器
 */
public class HotkeyManager {
    private static final Logger logger = LoggerFactory.getLogger(HotkeyManager.class);
    
    private static HotkeyManager instance;
    
    private final Map<String, HotkeyBinding> bindings = new ConcurrentHashMap<>();
    private final List<HotkeyListener> listeners = new ArrayList<>();
    
    // 默认热键
    public static final String NEW_CONNECTION = "new_connection";
    public static final String CLOSE_TAB = "close_tab";
    public static final String NEXT_TAB = "next_tab";
    public static final String PREV_TAB = "prev_tab";
    public static final String COPY = "copy";
    public static final String PASTE = "paste";
    public static final String FIND = "find";
    public static final String CLEAR_SCREEN = "clear_screen";
    public static final String RECONNECT = "reconnect";
    public static final String DISCONNECT = "disconnect";
    public static final String SFTP = "sftp";
    public static final String QUICK_COMMAND = "quick_command";
    
    private HotkeyManager() {
        initDefaultBindings();
    }
    
    public static synchronized HotkeyManager getInstance() {
        if (instance == null) {
            instance = new HotkeyManager();
        }
        return instance;
    }
    
    private void initDefaultBindings() {
        // 连接管理
        registerDefault(NEW_CONNECTION, "新建连接", KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
        registerDefault(CLOSE_TAB, "关闭标签", KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK);
        registerDefault(RECONNECT, "重新连接", KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        registerDefault(DISCONNECT, "断开连接", KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        
        // 标签切换
        registerDefault(NEXT_TAB, "下一个标签", KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK);
        registerDefault(PREV_TAB, "上一个标签", KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        
        // 编辑
        registerDefault(COPY, "复制", KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        registerDefault(PASTE, "粘贴", KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        registerDefault(FIND, "查找", KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
        
        // 终端
        registerDefault(CLEAR_SCREEN, "清屏", KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);
        
        // 功能
        registerDefault(SFTP, "打开SFTP", KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        registerDefault(QUICK_COMMAND, "快捷命令", KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK);
    }
    
    private void registerDefault(String id, String name, int keyCode, int modifiers) {
        HotkeyBinding binding = new HotkeyBinding(id, name, keyCode, modifiers);
        bindings.put(id, binding);
    }
    
    /**
     * 注册热键
     */
    public void register(String id, String name, int keyCode, int modifiers) {
        HotkeyBinding binding = new HotkeyBinding(id, name, keyCode, modifiers);
        bindings.put(id, binding);
        notifyListeners();
    }
    
    /**
     * 更新热键
     */
    public void update(String id, int keyCode, int modifiers) {
        HotkeyBinding binding = bindings.get(id);
        if (binding != null) {
            binding.setKeyCode(keyCode);
            binding.setModifiers(modifiers);
            notifyListeners();
        }
    }
    
    /**
     * 获取热键
     */
    public HotkeyBinding get(String id) {
        return bindings.get(id);
    }
    
    /**
     * 获取所有热键
     */
    public Collection<HotkeyBinding> getAll() {
        return bindings.values();
    }
    
    /**
     * 获取所有热键配置 (for HotkeyManagerDialog)
     */
    public java.util.List<HotkeyConfig> getHotkeys() {
        java.util.List<HotkeyConfig> list = new java.util.ArrayList<>();
        for (HotkeyBinding b : bindings.values()) {
            HotkeyConfig config = new HotkeyConfig();
            config.setName(b.getName());
            config.setActionName(b.getId());
            config.setKeyCode(b.getKeyCode());
            config.setModifiers(b.getModifiers());
            list.add(config);
        }
        return list;
    }
    
    public void addHotkey(HotkeyConfig config) {
        register(config.getActionName(), config.getName(), config.getKeyCode(), config.getModifiers());
    }
    
    public void updateHotkey(HotkeyConfig config) {
        update(config.getActionName(), config.getKeyCode(), config.getModifiers());
    }
    
    public void removeHotkey(HotkeyConfig config) {
        bindings.remove(config.getActionName());
        notifyListeners();
    }
    
    /**
     * 获取KeyStroke
     */
    public KeyStroke getKeyStroke(String id) {
        HotkeyBinding binding = bindings.get(id);
        if (binding != null) {
            return KeyStroke.getKeyStroke(binding.getKeyCode(), binding.getModifiers());
        }
        return null;
    }
    
    /**
     * 根据KeyStroke查找热键ID
     */
    public String findByKeyStroke(KeyStroke keyStroke) {
        for (Map.Entry<String, HotkeyBinding> entry : bindings.entrySet()) {
            HotkeyBinding b = entry.getValue();
            if (b.getKeyCode() == keyStroke.getKeyCode() && 
                b.getModifiers() == keyStroke.getModifiers()) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * 应用到组件
     */
    public void applyTo(JComponent component, String actionId, Action action) {
        KeyStroke keyStroke = getKeyStroke(actionId);
        if (keyStroke != null) {
            component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionId);
            component.getActionMap().put(actionId, action);
        }
    }
    
    /**
     * 检查热键冲突
     */
    public boolean hasConflict(String id, int keyCode, int modifiers) {
        for (Map.Entry<String, HotkeyBinding> entry : bindings.entrySet()) {
            if (!entry.getKey().equals(id)) {
                HotkeyBinding b = entry.getValue();
                if (b.getKeyCode() == keyCode && b.getModifiers() == modifiers) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Process key event
     */
    public boolean processKeyEvent(java.awt.event.KeyEvent e) {
        KeyStroke pressed = KeyStroke.getKeyStrokeForEvent(e);
        String actionId = findByKeyStroke(pressed);
        return actionId != null;
    }
    
    /**
     * 重置为默认
     */
    public void resetToDefault() {
        bindings.clear();
        initDefaultBindings();
        notifyListeners();
    }
    
    public void addListener(HotkeyListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(HotkeyListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners() {
        for (HotkeyListener l : listeners) {
            l.onHotkeysChanged();
        }
    }
    
    /**
     * 热键监听器
     */
    public interface HotkeyListener {
        void onHotkeysChanged();
    }
    
    /**
     * 热键绑定
     */
    public static class HotkeyBinding {
        private String id;
        private String name;
        private int keyCode;
        private int modifiers;
        
        public HotkeyBinding(String id, String name, int keyCode, int modifiers) {
            this.id = id;
            this.name = name;
            this.keyCode = keyCode;
            this.modifiers = modifiers;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        
        public int getKeyCode() { return keyCode; }
        public void setKeyCode(int keyCode) { this.keyCode = keyCode; }
        
        public int getModifiers() { return modifiers; }
        public void setModifiers(int modifiers) { this.modifiers = modifiers; }
        
        public String getKeyText() {
            StringBuilder sb = new StringBuilder();
            if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) sb.append("Ctrl+");
            if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) sb.append("Shift+");
            if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) sb.append("Alt+");
            if ((modifiers & InputEvent.META_DOWN_MASK) != 0) sb.append("Meta+");
            sb.append(KeyEvent.getKeyText(keyCode));
            return sb.toString();
        }
        
        public KeyStroke getKeyStroke() {
            return KeyStroke.getKeyStroke(keyCode, modifiers);
        }
    }
}
