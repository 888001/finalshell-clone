package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * FTP路径自动完成字段
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FtpPathAF extends JTextField {
    
    private JPopupMenu popup;
    private JList<String> suggestionList;
    private DefaultListModel<String> listModel;
    private List<String> pathHistory;
    private boolean ignoreDocumentChange;
    
    public FtpPathAF() {
        this(20);
    }
    
    public FtpPathAF(int columns) {
        super(columns);
        this.pathHistory = new ArrayList<>();
        initComponents();
        initListeners();
    }
    
    private void initComponents() {
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        popup = new JPopupMenu();
        JScrollPane scrollPane = new JScrollPane(suggestionList);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        popup.add(scrollPane);
        popup.setFocusable(false);
    }
    
    private void initListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (popup.isVisible() && !listModel.isEmpty()) {
                        int index = suggestionList.getSelectedIndex();
                        if (index < listModel.size() - 1) {
                            suggestionList.setSelectedIndex(index + 1);
                        }
                        e.consume();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (popup.isVisible() && !listModel.isEmpty()) {
                        int index = suggestionList.getSelectedIndex();
                        if (index > 0) {
                            suggestionList.setSelectedIndex(index - 1);
                        }
                        e.consume();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (popup.isVisible()) {
                        selectSuggestion();
                        e.consume();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popup.setVisible(false);
                }
            }
        });
        
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectSuggestion();
                }
            }
        });
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!popup.isVisible()) {
                    return;
                }
                SwingUtilities.invokeLater(() -> popup.setVisible(false));
            }
        });
    }
    
    private void selectSuggestion() {
        String selected = suggestionList.getSelectedValue();
        if (selected != null) {
            ignoreDocumentChange = true;
            setText(selected);
            ignoreDocumentChange = false;
            popup.setVisible(false);
        }
    }
    
    public void showSuggestions(List<String> suggestions) {
        listModel.clear();
        for (String s : suggestions) {
            listModel.addElement(s);
        }
        
        if (!listModel.isEmpty()) {
            suggestionList.setSelectedIndex(0);
            popup.show(this, 0, getHeight());
        } else {
            popup.setVisible(false);
        }
    }
    
    public void addToHistory(String path) {
        if (path != null && !path.isEmpty() && !pathHistory.contains(path)) {
            pathHistory.add(0, path);
            if (pathHistory.size() > 50) {
                pathHistory.remove(pathHistory.size() - 1);
            }
        }
    }
    
    public List<String> getHistory() {
        return new ArrayList<>(pathHistory);
    }
    
    public void setHistory(List<String> history) {
        this.pathHistory = new ArrayList<>(history);
    }
    
    public void clearHistory() {
        pathHistory.clear();
    }
}
