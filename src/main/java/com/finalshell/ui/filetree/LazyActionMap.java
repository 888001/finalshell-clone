package com.finalshell.ui.filetree;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 懒加载ActionMap
 * 延迟加载Action直到首次需要时
 */
public class LazyActionMap extends ActionMap {
    
    private transient Object loader;
    private Map<Object, Action> lazyActions;
    private boolean loaded;
    
    public LazyActionMap(Class<?> loaderClass) {
        this.loader = loaderClass;
        this.lazyActions = new HashMap<>();
        this.loaded = false;
    }
    
    public void put(Object key, Action action) {
        if (action == null) {
            remove(key);
        } else {
            if (loaded) {
                super.put(key, action);
            } else {
                lazyActions.put(key, action);
            }
        }
    }
    
    public Action get(Object key) {
        ensureLoaded();
        Action action = super.get(key);
        if (action == null) {
            action = lazyActions.get(key);
        }
        return action;
    }
    
    public void remove(Object key) {
        super.remove(key);
        lazyActions.remove(key);
    }
    
    public void clear() {
        super.clear();
        lazyActions.clear();
    }
    
    public Object[] keys() {
        ensureLoaded();
        Object[] superKeys = super.keys();
        Object[] lazyKeys = lazyActions.keySet().toArray();
        
        if (superKeys == null && lazyKeys.length == 0) {
            return null;
        }
        
        if (superKeys == null) {
            return lazyKeys;
        }
        
        if (lazyKeys.length == 0) {
            return superKeys;
        }
        
        Object[] allKeys = new Object[superKeys.length + lazyKeys.length];
        System.arraycopy(superKeys, 0, allKeys, 0, superKeys.length);
        System.arraycopy(lazyKeys, 0, allKeys, superKeys.length, lazyKeys.length);
        return allKeys;
    }
    
    public int size() {
        ensureLoaded();
        return super.size() + lazyActions.size();
    }
    
    private void ensureLoaded() {
        if (!loaded && loader != null) {
            loaded = true;
            loadActions();
        }
    }
    
    private void loadActions() {
        for (Map.Entry<Object, Action> entry : lazyActions.entrySet()) {
            super.put(entry.getKey(), entry.getValue());
        }
    }
    
    public static void installLazyActionMap(JComponent c, Class<?> loaderClass, String defaultsKey) {
        ActionMap map = (ActionMap) UIManager.get(defaultsKey);
        if (map == null) {
            map = new LazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        SwingUtilities.replaceUIActionMap(c, map);
    }
    
    public static ActionMap getActionMap(Class<?> loaderClass, String defaultsKey) {
        ActionMap map = (ActionMap) UIManager.get(defaultsKey);
        if (map == null) {
            map = new LazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        return map;
    }
}
