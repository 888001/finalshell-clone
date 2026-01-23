package com.finalshell.command;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * 自动弹出命令列表
 * 提供命令自动完成和历史命令弹出功能
 */
public class AutoPopupList extends JPopupMenu {
    
    private JList<String> list;
    private DefaultListModel<String> model;
    private JTextField textField;
    private List<String> allItems = new ArrayList<>();
    private boolean autoComplete = true;
    private int maxVisibleItems = 10;
    
    public AutoPopupList(JTextField textField) {
        this.textField = textField;
        initUI();
        initListeners();
    }
    
    private void initUI() {
        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(maxVisibleItems);
        
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        add(scrollPane);
        
        setFocusable(false);
    }
    
    private void initListeners() {
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectItem();
                }
            }
        });
        
        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectItem();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
            }
        });
        
        if (textField != null) {
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { filterItems(); }
                @Override
                public void removeUpdate(DocumentEvent e) { filterItems(); }
                @Override
                public void changedUpdate(DocumentEvent e) { filterItems(); }
            });
        }
    }
    
    public void setItems(List<String> items) {
        this.allItems = new ArrayList<>(items);
        updateList(allItems);
    }
    
    public void addItem(String item) {
        if (!allItems.contains(item)) {
            allItems.add(0, item);
        }
    }
    
    private void filterItems() {
        if (!autoComplete) return;
        String text = textField.getText().toLowerCase();
        if (text.isEmpty()) {
            updateList(allItems);
        } else {
            List<String> filtered = new ArrayList<>();
            for (String item : allItems) {
                if (item.toLowerCase().contains(text)) {
                    filtered.add(item);
                }
            }
            updateList(filtered);
        }
        if (model.size() > 0 && !isVisible()) {
            showPopup();
        }
    }
    
    private void updateList(List<String> items) {
        model.clear();
        int count = Math.min(items.size(), maxVisibleItems * 2);
        for (int i = 0; i < count; i++) {
            model.addElement(items.get(i));
        }
    }
    
    private void selectItem() {
        String selected = list.getSelectedValue();
        if (selected != null && textField != null) {
            textField.setText(selected);
            setVisible(false);
        }
    }
    
    public void showPopup() {
        if (textField != null && model.size() > 0) {
            show(textField, 0, textField.getHeight());
        }
    }
    
    public void setAutoComplete(boolean auto) { this.autoComplete = auto; }
    public boolean isAutoComplete() { return autoComplete; }
    public void setMaxVisibleItems(int max) { this.maxVisibleItems = max; }
}
