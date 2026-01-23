package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 标签按钮
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSH_Terminal_Analysis.md - TabButton
 */
public class TabButton extends JPanel {
    
    private JLabel titleLabel;
    private JButton closeButton;
    private TabWrap tabWrap;
    private boolean selected;
    private boolean hover;
    private TabListener tabListener;
    
    private static final Color SELECTED_BG = new Color(255, 255, 255);
    private static final Color NORMAL_BG = new Color(240, 240, 240);
    private static final Color HOVER_BG = new Color(245, 245, 245);
    
    public TabButton(TabWrap tabWrap) {
        this.tabWrap = tabWrap;
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 0));
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        
        titleLabel = new JLabel(tabWrap.getTitle());
        titleLabel.setIcon(tabWrap.getIcon());
        add(titleLabel, BorderLayout.CENTER);
        
        if (tabWrap.isCloseable()) {
            closeButton = new JButton("×");
            closeButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            closeButton.setMargin(new Insets(0, 2, 0, 2));
            closeButton.setBorderPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setFocusPainted(false);
            closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            closeButton.addActionListener(e -> {
                if (tabListener != null) {
                    tabListener.onTabClose(new TabEvent(TabEvent.TYPE_CLOSE, tabWrap));
                }
            });
            add(closeButton, BorderLayout.EAST);
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                updateBackground();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                updateBackground();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tabListener != null) {
                    tabListener.onTabSelected(new TabEvent(TabEvent.TYPE_SELECT, tabWrap));
                }
            }
        });
        
        updateBackground();
    }
    
    private void updateBackground() {
        if (selected) {
            setBackground(SELECTED_BG);
        } else if (hover) {
            setBackground(HOVER_BG);
        } else {
            setBackground(NORMAL_BG);
        }
    }
    
    public void setText(String text) {
        titleLabel.setText(text);
    }
    
    public void setIcon(Icon icon) {
        titleLabel.setIcon(icon);
    }
    
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { 
        this.selected = selected;
        updateBackground();
    }
    
    public TabWrap getTabWrap() { return tabWrap; }
    
    public void setTabListener(TabListener listener) {
        this.tabListener = listener;
    }
}
