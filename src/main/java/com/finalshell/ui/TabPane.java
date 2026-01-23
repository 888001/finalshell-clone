package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 标签页容器面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSH_Terminal_Analysis.md - TabPane
 */
public class TabPane extends JPanel implements TabListener {
    
    private JPanel tabButtonPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private List<TabWrap> tabs = new ArrayList<>();
    private TabWrap currentTab;
    private List<TabListener> listeners = new ArrayList<>();
    
    public TabPane() {
        setLayout(new BorderLayout());
        
        tabButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabButtonPanel.setBackground(new Color(240, 240, 240));
        add(tabButtonPanel, BorderLayout.NORTH);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    public void addTab(TabWrap tabWrap) {
        tabs.add(tabWrap);
        
        TabButton button = new TabButton(tabWrap);
        button.setTabListener(this);
        tabWrap.setTabButton(button);
        tabButtonPanel.add(button);
        
        String id = "tab_" + System.currentTimeMillis();
        tabWrap.setId(id);
        contentPanel.add(tabWrap.getPanel(), id);
        
        if (currentTab == null) {
            selectTab(tabWrap);
        }
        
        revalidate();
        repaint();
    }
    
    public void removeTab(TabWrap tabWrap) {
        int index = tabs.indexOf(tabWrap);
        if (index < 0) return;
        
        tabs.remove(tabWrap);
        tabButtonPanel.remove(tabWrap.getTabButton());
        contentPanel.remove(tabWrap.getPanel());
        
        tabWrap.getPanel().onClose();
        
        if (currentTab == tabWrap) {
            if (!tabs.isEmpty()) {
                int newIndex = Math.min(index, tabs.size() - 1);
                selectTab(tabs.get(newIndex));
            } else {
                currentTab = null;
            }
        }
        
        revalidate();
        repaint();
    }
    
    public void selectTab(TabWrap tabWrap) {
        if (currentTab != null) {
            currentTab.setActive(false);
            currentTab.getTabButton().setSelected(false);
            currentTab.getPanel().onDeactivated();
        }
        
        currentTab = tabWrap;
        currentTab.setActive(true);
        currentTab.getTabButton().setSelected(true);
        cardLayout.show(contentPanel, tabWrap.getId());
        currentTab.getPanel().onActivated();
    }
    
    public TabWrap getCurrentTab() { return currentTab; }
    
    public BaseTabPanel getCurrentPanel() {
        return currentTab != null ? currentTab.getPanel() : null;
    }
    
    public int getTabCount() { return tabs.size(); }
    
    public TabWrap getTabAt(int index) {
        return tabs.get(index);
    }
    
    public void addTabListener(TabListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void onTabSelected(TabEvent event) {
        selectTab(event.getTabWrap());
        for (TabListener l : listeners) {
            l.onTabSelected(event);
        }
    }
    
    @Override
    public void onTabClose(TabEvent event) {
        removeTab(event.getTabWrap());
        for (TabListener l : listeners) {
            l.onTabClose(event);
        }
    }
}
