package com.finalshell.theme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 主题管理器
 */
public class ThemeManager {
    private static final Logger logger = LoggerFactory.getLogger(ThemeManager.class);
    
    private static ThemeManager instance;
    
    private final Map<String, ThemeConfig> themes = new LinkedHashMap<>();
    private ThemeConfig currentTheme;
    private final List<ThemeChangeListener> listeners = new ArrayList<>();
    
    private ThemeManager() {
        registerBuiltinThemes();
        currentTheme = themes.get("light");
    }
    
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    private void registerBuiltinThemes() {
        themes.put("light", ThemeConfig.lightTheme());
        themes.put("dark", ThemeConfig.darkTheme());
        themes.put("dracula", ThemeConfig.draculaTheme());
        themes.put("monokai", ThemeConfig.monokaiTheme());
    }
    
    /**
     * 注册自定义主题
     */
    public void registerTheme(ThemeConfig theme) {
        themes.put(theme.getId(), theme);
    }
    
    /**
     * 获取所有主题
     */
    public Collection<ThemeConfig> getAllThemes() {
        return themes.values();
    }
    
    /**
     * 获取主题
     */
    public ThemeConfig getTheme(String id) {
        return themes.get(id);
    }
    
    /**
     * 获取当前主题
     */
    public ThemeConfig getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * 设置当前主题
     */
    public void setTheme(String themeId) {
        ThemeConfig theme = themes.get(themeId);
        if (theme != null && theme != currentTheme) {
            currentTheme = theme;
            applyTheme(theme);
            notifyListeners();
        }
    }
    
    /**
     * 应用主题到UI
     */
    public void applyTheme(ThemeConfig theme) {
        try {
            UIManager.put("Panel.background", new ColorUIResource(theme.getPanelColor()));
            UIManager.put("Button.background", new ColorUIResource(theme.getPanelColor()));
            UIManager.put("TextField.background", new ColorUIResource(theme.getInputColor()));
            UIManager.put("TextArea.background", new ColorUIResource(theme.getInputColor()));
            UIManager.put("Table.background", new ColorUIResource(theme.getBackgroundColor()));
            UIManager.put("Tree.background", new ColorUIResource(theme.getBackgroundColor()));
            UIManager.put("List.background", new ColorUIResource(theme.getBackgroundColor()));
            UIManager.put("ScrollPane.background", new ColorUIResource(theme.getBackgroundColor()));
            
            UIManager.put("Panel.foreground", new ColorUIResource(theme.getTextColor()));
            UIManager.put("Button.foreground", new ColorUIResource(theme.getTextColor()));
            UIManager.put("TextField.foreground", new ColorUIResource(theme.getTextColor()));
            UIManager.put("TextArea.foreground", new ColorUIResource(theme.getTextColor()));
            UIManager.put("Table.foreground", new ColorUIResource(theme.getTextColor()));
            UIManager.put("Tree.foreground", new ColorUIResource(theme.getTextColor()));
            UIManager.put("List.foreground", new ColorUIResource(theme.getTextColor()));
            UIManager.put("Label.foreground", new ColorUIResource(theme.getTextColor()));
            
            UIManager.put("Table.selectionBackground", new ColorUIResource(theme.getSelectionColor()));
            UIManager.put("Tree.selectionBackground", new ColorUIResource(theme.getSelectionColor()));
            UIManager.put("List.selectionBackground", new ColorUIResource(theme.getSelectionColor()));
            
            UIManager.put("TabbedPane.selected", new ColorUIResource(theme.getSelectionColor()));
            UIManager.put("TabbedPane.background", new ColorUIResource(theme.getPanelColor()));
            
            UIManager.put("MenuBar.background", new ColorUIResource(theme.getPanelColor()));
            UIManager.put("Menu.background", new ColorUIResource(theme.getPanelColor()));
            UIManager.put("MenuItem.background", new ColorUIResource(theme.getPanelColor()));
            UIManager.put("Menu.foreground", new ColorUIResource(theme.getTextColor()));
            UIManager.put("MenuItem.foreground", new ColorUIResource(theme.getTextColor()));
            
            UIManager.put("ToolBar.background", new ColorUIResource(theme.getPanelColor()));
            UIManager.put("SplitPane.background", new ColorUIResource(theme.getPanelColor()));
            
            logger.info("主题已应用: {}", theme.getName());
            
        } catch (Exception e) {
            logger.error("应用主题失败", e);
        }
    }
    
    /**
     * 刷新所有窗口
     */
    public void refreshAllWindows() {
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }
    
    /**
     * 应用主题到组件
     */
    public void applyToComponent(Component component) {
        if (currentTheme == null) return;
        
        if (component instanceof JPanel) {
            component.setBackground(currentTheme.getPanelColor());
            component.setForeground(currentTheme.getTextColor());
        } else if (component instanceof JTextField || component instanceof JTextArea) {
            component.setBackground(currentTheme.getInputColor());
            component.setForeground(currentTheme.getTextColor());
        } else if (component instanceof JTable || component instanceof JTree || component instanceof JList) {
            component.setBackground(currentTheme.getBackgroundColor());
            component.setForeground(currentTheme.getTextColor());
        } else if (component instanceof JLabel) {
            component.setForeground(currentTheme.getTextColor());
        }
        
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyToComponent(child);
            }
        }
    }
    
    public void addListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners() {
        for (ThemeChangeListener l : listeners) {
            l.onThemeChanged(currentTheme);
        }
    }
    
    /**
     * 主题变更监听器
     */
    public interface ThemeChangeListener {
        void onThemeChanged(ThemeConfig newTheme);
    }
}
