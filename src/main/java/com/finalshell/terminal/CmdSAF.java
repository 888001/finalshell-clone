package com.finalshell.terminal;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 命令智能自动填充
 * 提供命令行的智能提示和自动完成功能
 */
public class CmdSAF {
    
    private JTextComponent textComponent;
    private JPopupMenu popupMenu;
    private JList<String> suggestionList;
    private DefaultListModel<String> listModel;
    private List<String> commandHistory;
    private List<String> knownCommands;
    private boolean enabled = true;
    
    public CmdSAF(JTextComponent textComponent) {
        this.textComponent = textComponent;
        this.commandHistory = new ArrayList<>();
        this.knownCommands = new ArrayList<>();
        initComponents();
        initListeners();
    }
    
    private void initComponents() {
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setVisibleRowCount(8);
        
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    applySuggestion();
                }
            }
        });
        
        popupMenu = new JPopupMenu();
        popupMenu.setBorderPainted(true);
        JScrollPane scrollPane = new JScrollPane(suggestionList);
        scrollPane.setBorder(null);
        popupMenu.add(scrollPane);
    }
    
    private void initListeners() {
        textComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!enabled) return;
                
                if (popupMenu.isVisible()) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        selectNext();
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        selectPrevious();
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        applySuggestion();
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        hidePopup();
                        e.consume();
                    }
                } else {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                        showSuggestions();
                        e.consume();
                    }
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                if (!enabled) return;
                
                if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '-' || e.getKeyChar() == '_') {
                    updateSuggestions();
                }
            }
        });
    }
    
    private void showSuggestions() {
        updateSuggestions();
        if (!listModel.isEmpty()) {
            showPopup();
        }
    }
    
    private void updateSuggestions() {
        String text = getCurrentWord();
        if (text.isEmpty()) {
            hidePopup();
            return;
        }
        
        listModel.clear();
        String lowerText = text.toLowerCase();
        
        // 从历史命令中查找
        for (String cmd : commandHistory) {
            if (cmd.toLowerCase().startsWith(lowerText) && !listModel.contains(cmd)) {
                listModel.addElement(cmd);
            }
        }
        
        // 从已知命令中查找
        for (String cmd : knownCommands) {
            if (cmd.toLowerCase().startsWith(lowerText) && !listModel.contains(cmd)) {
                listModel.addElement(cmd);
            }
        }
        
        if (listModel.isEmpty()) {
            hidePopup();
        } else {
            suggestionList.setSelectedIndex(0);
            showPopup();
        }
    }
    
    private String getCurrentWord() {
        try {
            int caretPos = textComponent.getCaretPosition();
            String text = textComponent.getText();
            int start = caretPos;
            
            while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))) {
                start--;
            }
            
            return text.substring(start, caretPos);
        } catch (Exception e) {
            return "";
        }
    }
    
    private void applySuggestion() {
        String selected = suggestionList.getSelectedValue();
        if (selected != null) {
            try {
                int caretPos = textComponent.getCaretPosition();
                String text = textComponent.getText();
                int start = caretPos;
                
                while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))) {
                    start--;
                }
                
                Document doc = textComponent.getDocument();
                doc.remove(start, caretPos - start);
                doc.insertString(start, selected, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        hidePopup();
    }
    
    private void showPopup() {
        if (!popupMenu.isVisible() && !listModel.isEmpty()) {
            try {
                Rectangle r = textComponent.modelToView(textComponent.getCaretPosition());
                popupMenu.show(textComponent, r.x, r.y + r.height);
            } catch (BadLocationException e) {
                popupMenu.show(textComponent, 0, textComponent.getHeight());
            }
        }
    }
    
    private void hidePopup() {
        popupMenu.setVisible(false);
    }
    
    private void selectNext() {
        int index = suggestionList.getSelectedIndex();
        if (index < listModel.size() - 1) {
            suggestionList.setSelectedIndex(index + 1);
            suggestionList.ensureIndexIsVisible(index + 1);
        }
    }
    
    private void selectPrevious() {
        int index = suggestionList.getSelectedIndex();
        if (index > 0) {
            suggestionList.setSelectedIndex(index - 1);
            suggestionList.ensureIndexIsVisible(index - 1);
        }
    }
    
    public void addToHistory(String command) {
        if (command != null && !command.trim().isEmpty()) {
            commandHistory.remove(command);
            commandHistory.add(0, command);
            if (commandHistory.size() > 100) {
                commandHistory.remove(commandHistory.size() - 1);
            }
        }
    }
    
    public void setKnownCommands(List<String> commands) {
        this.knownCommands = commands != null ? new ArrayList<>(commands) : new ArrayList<>();
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            hidePopup();
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
