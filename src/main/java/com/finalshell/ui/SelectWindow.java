package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 选择窗口
 * 提供列表选择的弹出窗口
 */
public class SelectWindow extends JWindow {
    
    private JList<String> list;
    private DefaultListModel<String> listModel;
    private SelectListener selectListener;
    
    public SelectWindow(Window owner) {
        super(owner);
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(8);
        
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    confirmSelection();
                }
            }
        });
        
        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmSelection();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scrollPane, BorderLayout.CENTER);
        
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                setVisible(false);
            }
        });
    }
    
    public void setItems(List<String> items) {
        listModel.clear();
        if (items != null) {
            for (String item : items) {
                listModel.addElement(item);
            }
        }
        if (!listModel.isEmpty()) {
            list.setSelectedIndex(0);
        }
    }
    
    public void setItems(String[] items) {
        listModel.clear();
        if (items != null) {
            for (String item : items) {
                listModel.addElement(item);
            }
        }
        if (!listModel.isEmpty()) {
            list.setSelectedIndex(0);
        }
    }
    
    public void showAt(Component invoker, int x, int y) {
        Point p = invoker.getLocationOnScreen();
        setLocation(p.x + x, p.y + y);
        pack();
        setVisible(true);
        list.requestFocusInWindow();
    }
    
    public void showBelow(Component invoker) {
        showAt(invoker, 0, invoker.getHeight());
    }
    
    private void confirmSelection() {
        String selected = list.getSelectedValue();
        setVisible(false);
        if (selected != null && selectListener != null) {
            selectListener.onSelect(selected);
        }
    }
    
    public void setSelectListener(SelectListener listener) {
        this.selectListener = listener;
    }
    
    public String getSelectedValue() {
        return list.getSelectedValue();
    }
    
    public void selectNext() {
        int index = list.getSelectedIndex();
        if (index < listModel.size() - 1) {
            list.setSelectedIndex(index + 1);
            list.ensureIndexIsVisible(index + 1);
        }
    }
    
    public void selectPrevious() {
        int index = list.getSelectedIndex();
        if (index > 0) {
            list.setSelectedIndex(index - 1);
            list.ensureIndexIsVisible(index - 1);
        }
    }
    
    public interface SelectListener {
        void onSelect(String value);
    }
}
