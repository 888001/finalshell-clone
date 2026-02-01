package com.finalshell.ui;

import javax.swing.*;
import javax.accessibility.*;
import java.awt.*;
import java.util.Hashtable;

/**
 * 自定义分层面板 - 对齐原版myssh复杂实现
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: myssh/ui/MyLayeredPane.java (384行)
 */
public class MyLayeredPane extends JComponent implements Accessible {
    
    public static final Integer DEFAULT_LAYER = new Integer(0);
    public static final Integer PALETTE_LAYER = new Integer(100);
    public static final Integer MODAL_LAYER = new Integer(200);
    public static final Integer POPUP_LAYER = new Integer(300);
    public static final Integer DRAG_LAYER = new Integer(400);
    public static final Integer FRAME_CONTENT_LAYER = new Integer(-30000);
    
    public static final String LAYER_PROPERTY = "layeredContainerLayer";
    
    private Hashtable<Component, Integer> componentToLayer;
    private boolean optimizedDrawingPossible = true;
    private Component contentPane;
    
    public MyLayeredPane() {
        setLayout(null);
    }
    
    /**
     * 检查是否有分层组件，更新优化绘制状态
     */
    private void validateOptimizedDrawing() {
        boolean layeredComponentFound = false;
        synchronized (getTreeLock()) {
            Component[] components = getComponents();
            for (Component c : components) {
                Integer layer = null;
                if (c instanceof JComponent) {
                    layer = (Integer) ((JComponent) c).getClientProperty(LAYER_PROPERTY);
                }
                if (layer != null && !layer.equals(FRAME_CONTENT_LAYER)) {
                    layeredComponentFound = true;
                    break;
                }
            }
        }
        optimizedDrawingPossible = !layeredComponentFound;
    }
    
    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        int layer;
        if (constraints instanceof Integer) {
            layer = (Integer) constraints;
            setLayer(comp, layer);
        } else {
            layer = getLayer(comp);
        }
        int pos = insertIndexForLayer(layer, index);
        super.addImpl(comp, constraints, pos);
        comp.validate();
        comp.repaint();
        validateOptimizedDrawing();
    }
    
    @Override
    public void remove(int index) {
        Component c = getComponent(index);
        super.remove(index);
        if (c != null && !(c instanceof JComponent)) {
            getComponentToLayer().remove(c);
        }
        validateOptimizedDrawing();
    }
    
    @Override
    public void removeAll() {
        Component[] children = getComponents();
        Hashtable<Component, Integer> cToL = getComponentToLayer();
        for (Component c : children) {
            if (c != null && !(c instanceof JComponent)) {
                cToL.remove(c);
            }
        }
        super.removeAll();
    }
    
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return optimizedDrawingPossible;
    }
    
    /**
     * 设置组件层级
     */
    public static void putLayer(JComponent c, int layer) {
        Integer layerObj = new Integer(layer);
        c.putClientProperty(LAYER_PROPERTY, layerObj);
    }
    
    /**
     * 获取组件层级
     */
    public static int getLayer(JComponent c) {
        Integer i = (Integer) c.getClientProperty(LAYER_PROPERTY);
        if (i != null) {
            return i.intValue();
        }
        return DEFAULT_LAYER.intValue();
    }
    
    /**
     * 查找包含指定组件的MyLayeredPane
     */
    public static MyLayeredPane getLayeredPaneAbove(Component c) {
        if (c == null) return null;
        Container parent = c.getParent();
        while (parent != null && !(parent instanceof MyLayeredPane)) {
            parent = parent.getParent();
        }
        return (MyLayeredPane) parent;
    }
    
    /**
     * 设置组件层级
     */
    public void setLayer(Component c, int layer) {
        setLayer(c, layer, -1);
    }
    
    /**
     * 设置组件层级和位置
     */
    public void setLayer(Component c, int layer, int position) {
        Integer layerObj = getObjectForLayer(layer);
        if (layer == getLayer(c) && position == getPosition(c)) {
            repaint(c.getBounds());
            return;
        }
        
        if (c instanceof JComponent) {
            ((JComponent) c).putClientProperty(LAYER_PROPERTY, layerObj);
        } else {
            getComponentToLayer().put(c, layerObj);
        }
        
        if (c.getParent() == null || c.getParent() != this) {
            repaint(c.getBounds());
            return;
        }
        
        int index = insertIndexForLayer(c, layer, position);
        setComponentZOrder(c, index);
        repaint(c.getBounds());
    }
    
    /**
     * 获取组件层级
     */
    public int getLayer(Component c) {
        Integer i;
        if (c instanceof JComponent) {
            i = (Integer) ((JComponent) c).getClientProperty(LAYER_PROPERTY);
        } else {
            i = getComponentToLayer().get(c);
        }
        if (i == null) {
            return DEFAULT_LAYER.intValue();
        }
        return i.intValue();
    }
    
    /**
     * 获取组件在其层级中的位置
     */
    public int getPosition(Component c) {
        int startLocation = getIndexOf(c);
        if (startLocation == -1) {
            return -1;
        }
        
        int startLayer = getLayer(c);
        int pos = 0;
        for (int i = startLocation - 1; i >= 0; i--) {
            int curLayer = getLayer(getComponent(i));
            if (curLayer == startLayer) {
                pos++;
            } else {
                return pos;
            }
        }
        return pos;
    }
    
    /**
     * 获取组件索引
     */
    public int getIndexOf(Component c) {
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            if (c == getComponent(i)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 移动组件到顶层
     */
    public void moveToFront(Component c) {
        setPosition(c, 0);
    }
    
    /**
     * 移动组件到底层
     */
    public void moveToBack(Component c) {
        setPosition(c, -1);
    }
    
    /**
     * 设置组件在层级中的位置
     */
    public void setPosition(Component c, int position) {
        setLayer(c, getLayer(c), position);
    }
    
    /**
     * 获取最高层级
     */
    public int highestLayer() {
        if (getComponentCount() > 0) {
            return getLayer(getComponent(0));
        }
        return 0;
    }
    
    /**
     * 获取最低层级
     */
    public int lowestLayer() {
        int count = getComponentCount();
        if (count > 0) {
            return getLayer(getComponent(count - 1));
        }
        return 0;
    }
    
    /**
     * 获取指定层级的组件数量
     */
    public int getComponentCountInLayer(int layer) {
        int layerCount = 0;
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            int curLayer = getLayer(getComponent(i));
            if (curLayer == layer) {
                layerCount++;
            } else if (layerCount > 0 || curLayer < layer) {
                break;
            }
        }
        return layerCount;
    }
    
    /**
     * 获取指定层级的所有组件
     */
    public Component[] getComponentsInLayer(int layer) {
        int layerCount = 0;
        Component[] results = new Component[getComponentCountInLayer(layer)];
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            int curLayer = getLayer(getComponent(i));
            if (curLayer == layer) {
                results[layerCount++] = getComponent(i);
            } else if (layerCount > 0 || curLayer < layer) {
                break;
            }
        }
        return results;
    }
    
    @Override
    public void paint(Graphics g) {
        if (isOpaque()) {
            Rectangle r = g.getClipBounds();
            Color c = getBackground();
            if (c == null) {
                c = Color.lightGray;
            }
            g.setColor(c);
            if (r != null) {
                g.fillRect(r.x, r.y, r.width, r.height);
            } else {
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
        super.paint(g);
    }
    
    protected Hashtable<Component, Integer> getComponentToLayer() {
        if (componentToLayer == null) {
            componentToLayer = new Hashtable<Component, Integer>(4);
        }
        return componentToLayer;
    }
    
    protected Integer getObjectForLayer(int layer) {
        Integer layerObj;
        switch (layer) {
            case 0:
                layerObj = DEFAULT_LAYER;
                break;
            case 100:
                layerObj = PALETTE_LAYER;
                break;
            case 200:
                layerObj = MODAL_LAYER;
                break;
            case 300:
                layerObj = POPUP_LAYER;
                break;
            case 400:
                layerObj = DRAG_LAYER;
                break;
            default:
                layerObj = new Integer(layer);
        }
        return layerObj;
    }
    
    protected int insertIndexForLayer(int layer, int position) {
        return insertIndexForLayer(null, layer, position);
    }
    
    private int insertIndexForLayer(Component comp, int layer, int position) {
        int layerStart = -1;
        int layerEnd = -1;
        int componentCount = getComponentCount();
        java.util.ArrayList<Component> compList = new java.util.ArrayList<Component>(componentCount);
        
        for (int index = 0; index < componentCount; index++) {
            if (getComponent(index) != comp) {
                compList.add(getComponent(index));
            }
        }
        
        int count = compList.size();
        for (int i = 0; i < count; i++) {
            int curLayer = getLayer(compList.get(i));
            if (layerStart == -1 && curLayer == layer) {
                layerStart = i;
            }
            if (curLayer < layer) {
                if (i == 0) {
                    layerStart = 0;
                    layerEnd = 0;
                    break;
                }
                layerEnd = i;
                break;
            }
        }
        
        if (layerStart == -1 && layerEnd == -1) {
            return count;
        }
        if (layerStart != -1 && layerEnd == -1) {
            layerEnd = count;
        }
        if (layerEnd != -1 && layerStart == -1) {
            layerStart = layerEnd;
        }
        if (position == -1) {
            return layerEnd;
        }
        if (position > -1 && layerStart + position <= layerEnd) {
            return layerStart + position;
        }
        return layerEnd;
    }
    
    // 便利方法
    public void setContentPane(Component content) {
        if (contentPane != null) {
            remove(contentPane);
        }
        contentPane = content;
        add(content, FRAME_CONTENT_LAYER);
        revalidate();
    }
    
    public Component getContentPane() {
        return contentPane;
    }
    
    public void addToLayer(Component comp, Integer layer) {
        add(comp, layer);
    }
    
    public void addToDefaultLayer(Component comp) {
        add(comp, DEFAULT_LAYER);
    }
    
    public void addToPopupLayer(Component comp) {
        add(comp, POPUP_LAYER);
    }
    
    public void addToModalLayer(Component comp) {
        add(comp, MODAL_LAYER);
    }
    
    public void addToDragLayer(Component comp) {
        add(comp, DRAG_LAYER);
    }
    
    @Override
    public void doLayout() {
        if (contentPane != null) {
            contentPane.setBounds(0, 0, getWidth(), getHeight());
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (contentPane != null) {
            return contentPane.getPreferredSize();
        }
        return super.getPreferredSize();
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleMyLayeredPane();
        }
        return accessibleContext;
    }
    
    protected class AccessibleMyLayeredPane extends AccessibleJComponent {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LAYERED_PANE;
        }
    }
}
