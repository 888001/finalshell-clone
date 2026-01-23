package com.finalshell.ui.panel;

import com.finalshell.ui.layout.OverLayoutManager;
import javax.swing.*;
import java.awt.*;

/**
 * 覆盖面板
 * 使用OverLayoutManager将子组件堆叠显示
 */
public class OverPanel extends JPanel {
    
    public OverPanel() {
        setLayout(new OverLayoutManager());
        setOpaque(false);
    }
    
    /**
     * 显示指定组件，隐藏其他组件
     */
    public void showComponent(Component comp) {
        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponent(i);
            c.setVisible(c == comp);
        }
        revalidate();
        repaint();
    }
    
    /**
     * 通过名称显示组件
     */
    public void showComponent(String name) {
        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponent(i);
            c.setVisible(name.equals(c.getName()));
        }
        revalidate();
        repaint();
    }
    
    /**
     * 通过索引显示组件
     */
    public void showComponent(int index) {
        for (int i = 0; i < getComponentCount(); i++) {
            getComponent(i).setVisible(i == index);
        }
        revalidate();
        repaint();
    }
    
    /**
     * 获取当前可见的组件
     */
    public Component getVisibleComponent() {
        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponent(i);
            if (c.isVisible()) {
                return c;
            }
        }
        return null;
    }
    
    /**
     * 添加命名组件
     */
    public void addComponent(String name, Component comp) {
        comp.setName(name);
        add(comp);
    }
}
