package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 根缓存面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - RootCachePanel
 */
public class RootCachePanel extends JPanel {
    
    private Map<String, JComponent> panelCache = new HashMap<>();
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String currentPanel;
    
    public RootCachePanel() {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    public void addPanel(String name, JComponent panel) {
        if (!panelCache.containsKey(name)) {
            panelCache.put(name, panel);
            contentPanel.add(panel, name);
        }
    }
    
    public void showPanel(String name) {
        if (panelCache.containsKey(name)) {
            cardLayout.show(contentPanel, name);
            currentPanel = name;
        }
    }
    
    public JComponent getPanel(String name) {
        return panelCache.get(name);
    }
    
    public void removePanel(String name) {
        JComponent panel = panelCache.remove(name);
        if (panel != null) {
            contentPanel.remove(panel);
        }
    }
    
    public boolean hasPanel(String name) {
        return panelCache.containsKey(name);
    }
    
    public String getCurrentPanel() {
        return currentPanel;
    }
    
    public List<String> getPanelNames() {
        return new ArrayList<>(panelCache.keySet());
    }
    
    public void clearCache() {
        panelCache.clear();
        contentPanel.removeAll();
        currentPanel = null;
    }
    
    public int getPanelCount() {
        return panelCache.size();
    }
}
