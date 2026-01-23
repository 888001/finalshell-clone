package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * 标签栏组件
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: TabBar_UI_DeepAnalysis.md
 */
public class TabBar extends JPanel {
    
    private final List<TabButton> tabs = new ArrayList<>();
    private final List<TabListener> listeners = new ArrayList<>();
    private final JPanel tabPanel;
    private final JButton menuButton;
    private TabButton selectedTab;
    
    private static final Color SELECTED_BG = new Color(240, 240, 240);
    private static final Color HOVER_BG = new Color(230, 230, 230);
    private static final Color NORMAL_BG = new Color(245, 245, 245);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    
    public TabBar() {
        setLayout(new BorderLayout());
        setBackground(NORMAL_BG);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);
        add(tabPanel, BorderLayout.CENTER);
        
        // 右侧菜单按钮
        menuButton = new JButton("▼");
        menuButton.setFont(menuButton.getFont().deriveFont(10f));
        menuButton.setMargin(new Insets(2, 5, 2, 5));
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> showTabMenu());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        rightPanel.setOpaque(false);
        rightPanel.add(menuButton);
        add(rightPanel, BorderLayout.EAST);
    }
    
    public TabButton addTab(String title, Icon icon, Component content) {
        TabButton tab = new TabButton(title, icon, content);
        tabs.add(tab);
        tabPanel.add(tab);
        
        if (selectedTab == null) {
            selectTab(tab);
        }
        
        revalidate();
        repaint();
        return tab;
    }
    
    public void removeTab(TabButton tab) {
        int index = tabs.indexOf(tab);
        if (index >= 0) {
            tabs.remove(tab);
            tabPanel.remove(tab);
            
            if (tab == selectedTab && !tabs.isEmpty()) {
                int newIndex = Math.min(index, tabs.size() - 1);
                selectTab(tabs.get(newIndex));
            } else if (tabs.isEmpty()) {
                selectedTab = null;
            }
            
            fireTabClosed(tab);
            revalidate();
            repaint();
        }
    }
    
    public void selectTab(TabButton tab) {
        if (selectedTab != null) {
            selectedTab.setSelected(false);
        }
        
        selectedTab = tab;
        if (tab != null) {
            tab.setSelected(true);
            fireTabSelected(tab);
        }
    }
    
    public void selectTab(int index) {
        if (index >= 0 && index < tabs.size()) {
            selectTab(tabs.get(index));
        }
    }
    
    public TabButton getSelectedTab() {
        return selectedTab;
    }
    
    public int getTabCount() {
        return tabs.size();
    }
    
    public TabButton getTab(int index) {
        return tabs.get(index);
    }
    
    public int indexOfTab(TabButton tab) {
        return tabs.indexOf(tab);
    }
    
    public void setTabTitle(TabButton tab, String title) {
        tab.setTitle(title);
    }
    
    private void showTabMenu() {
        if (tabs.isEmpty()) return;
        
        JPopupMenu menu = new JPopupMenu();
        
        for (TabButton tab : tabs) {
            JMenuItem item = new JMenuItem(tab.getTitle());
            item.addActionListener(e -> selectTab(tab));
            if (tab == selectedTab) {
                item.setFont(item.getFont().deriveFont(Font.BOLD));
            }
            menu.add(item);
        }
        
        menu.addSeparator();
        
        JMenuItem closeAll = new JMenuItem("关闭所有标签");
        closeAll.addActionListener(e -> {
            for (TabButton tab : new ArrayList<>(tabs)) {
                removeTab(tab);
            }
        });
        menu.add(closeAll);
        
        menu.show(menuButton, 0, menuButton.getHeight());
    }
    
    public void addTabListener(TabListener listener) {
        listeners.add(listener);
    }
    
    public void removeTabListener(TabListener listener) {
        listeners.remove(listener);
    }
    
    private void fireTabSelected(TabButton tab) {
        for (TabListener l : listeners) {
            l.tabSelected(tab);
        }
    }
    
    private void fireTabClosed(TabButton tab) {
        for (TabListener l : listeners) {
            l.tabClosed(tab);
        }
    }
    
    /**
     * 标签按钮
     */
    public class TabButton extends JPanel {
        
        private final JLabel titleLabel;
        private final JButton closeButton;
        private final Component content;
        private boolean selected;
        
        public TabButton(String title, Icon icon, Component content) {
            this.content = content;
            
            setLayout(new BorderLayout(5, 0));
            setOpaque(true);
            setBackground(NORMAL_BG);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 5)
            ));
            
            titleLabel = new JLabel(title, icon, SwingConstants.LEFT);
            titleLabel.setFont(titleLabel.getFont().deriveFont(12f));
            add(titleLabel, BorderLayout.CENTER);
            
            closeButton = new JButton("×");
            closeButton.setFont(closeButton.getFont().deriveFont(Font.BOLD, 12f));
            closeButton.setMargin(new Insets(0, 3, 0, 3));
            closeButton.setFocusPainted(false);
            closeButton.setBorderPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.addActionListener(e -> removeTab(this));
            closeButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    closeButton.setForeground(Color.RED);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    closeButton.setForeground(Color.BLACK);
                }
            });
            add(closeButton, BorderLayout.EAST);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        selectTab(TabButton.this);
                    } else if (e.getButton() == MouseEvent.BUTTON2) {
                        removeTab(TabButton.this);
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!selected) setBackground(HOVER_BG);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!selected) setBackground(NORMAL_BG);
                }
            });
            
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        
        public String getTitle() {
            return titleLabel.getText();
        }
        
        public void setTitle(String title) {
            titleLabel.setText(title);
        }
        
        public Component getContent() {
            return content;
        }
        
        public boolean isSelected() {
            return selected;
        }
        
        public void setSelected(boolean selected) {
            this.selected = selected;
            setBackground(selected ? SELECTED_BG : NORMAL_BG);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, selected ? 0 : 0, 1, BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 5)
            ));
        }
    }
    
    /**
     * 标签监听器接口
     */
    public interface TabListener {
        void tabSelected(TabButton tab);
        void tabClosed(TabButton tab);
    }
}
