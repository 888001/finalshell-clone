package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 卡片布局面板 - 用于切换多个子面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Panel_Module_Analysis.md
 */
public class CardPanel extends JPanel {
    
    private final CardLayout cardLayout;
    
    public CardPanel() {
        this.cardLayout = new CardLayout();
        setLayout(cardLayout);
    }
    
    /**
     * 添加卡片
     */
    public void addCard(JComponent component, String name) {
        add(component, name);
    }
    
    /**
     * 添加卡片 (使用组件名称)
     */
    public void addCard(JComponent component) {
        String name = component.getName();
        if (name == null) {
            name = component.getClass().getSimpleName() + "_" + System.identityHashCode(component);
        }
        add(component, name);
    }
    
    /**
     * 显示指定名称的卡片
     */
    public void show(String name) {
        cardLayout.show(this, name);
    }
    
    /**
     * 显示指定组件
     */
    public void show(JComponent component) {
        String name = component.getName();
        if (name == null) {
            name = component.getClass().getSimpleName() + "_" + System.identityHashCode(component);
        }
        cardLayout.show(this, name);
    }
    
    /**
     * 显示下一个卡片
     */
    public void next() {
        cardLayout.next(this);
    }
    
    /**
     * 显示上一个卡片
     */
    public void previous() {
        cardLayout.previous(this);
    }
    
    /**
     * 显示第一个卡片
     */
    public void first() {
        cardLayout.first(this);
    }
    
    /**
     * 显示最后一个卡片
     */
    public void last() {
        cardLayout.last(this);
    }
}
