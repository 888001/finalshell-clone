package com.finalshell.terminal;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * 命令自动完成输入框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AutoCompleteCmdAF extends JTextField {
    
    private JPopupMenu popup;
    private JList<String> suggestionList;
    private DefaultListModel<String> listModel;
    private List<String> commandHistory;
    private List<String> knownCommands;
    private int maxHistory = 100;
    
    public AutoCompleteCmdAF() {
        this.commandHistory = new ArrayList<>();
        this.knownCommands = new ArrayList<>();
        initUI();
        initDefaultCommands();
    }
    
    private void initUI() {
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setVisibleRowCount(8);
        
        popup = new JPopupMenu();
        popup.add(new JScrollPane(suggestionList));
        popup.setFocusable(false);
        
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectSuggestion();
                }
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (popup.isVisible()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            moveSelection(-1);
                            e.consume();
                            break;
                        case KeyEvent.VK_DOWN:
                            moveSelection(1);
                            e.consume();
                            break;
                        case KeyEvent.VK_ENTER:
                            if (suggestionList.getSelectedIndex() >= 0) {
                                selectSuggestion();
                                e.consume();
                            }
                            break;
                        case KeyEvent.VK_ESCAPE:
                            popup.setVisible(false);
                            e.consume();
                            break;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    showHistory();
                    e.consume();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                if (!Character.isISOControl(e.getKeyChar()) && 
                    e.getKeyCode() != KeyEvent.VK_UP && 
                    e.getKeyCode() != KeyEvent.VK_DOWN) {
                    updateSuggestions();
                }
            }
        });
    }
    
    private void initDefaultCommands() {
        String[] defaults = {
            "ls", "cd", "pwd", "cat", "grep", "find", "mkdir", "rm", "cp", "mv",
            "chmod", "chown", "ps", "top", "kill", "df", "du", "free", "uname",
            "tar", "gzip", "gunzip", "ssh", "scp", "rsync", "wget", "curl",
            "vim", "nano", "less", "more", "head", "tail", "wc", "sort", "uniq",
            "awk", "sed", "cut", "tr", "diff", "history", "man", "which", "whereis"
        };
        knownCommands.addAll(Arrays.asList(defaults));
    }
    
    private void updateSuggestions() {
        String text = getText().trim();
        if (text.isEmpty()) {
            popup.setVisible(false);
            return;
        }
        
        List<String> matches = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        for (String cmd : commandHistory) {
            if (cmd.toLowerCase().startsWith(lowerText) && !matches.contains(cmd)) {
                matches.add(cmd);
            }
        }
        
        for (String cmd : knownCommands) {
            if (cmd.toLowerCase().startsWith(lowerText) && !matches.contains(cmd)) {
                matches.add(cmd);
            }
        }
        
        if (matches.isEmpty()) {
            popup.setVisible(false);
            return;
        }
        
        listModel.clear();
        for (String match : matches) {
            listModel.addElement(match);
        }
        
        suggestionList.setSelectedIndex(0);
        popup.show(this, 0, getHeight());
        requestFocus();
    }
    
    private void showHistory() {
        if (commandHistory.isEmpty()) return;
        
        listModel.clear();
        for (int i = commandHistory.size() - 1; i >= 0 && listModel.size() < 20; i--) {
            listModel.addElement(commandHistory.get(i));
        }
        
        if (listModel.size() > 0) {
            suggestionList.setSelectedIndex(0);
            popup.show(this, 0, getHeight());
            requestFocus();
        }
    }
    
    private void moveSelection(int delta) {
        int index = suggestionList.getSelectedIndex() + delta;
        if (index >= 0 && index < listModel.size()) {
            suggestionList.setSelectedIndex(index);
            suggestionList.ensureIndexIsVisible(index);
        }
    }
    
    private void selectSuggestion() {
        String selected = suggestionList.getSelectedValue();
        if (selected != null) {
            setText(selected);
            setCaretPosition(getText().length());
        }
        popup.setVisible(false);
    }
    
    public void addToHistory(String command) {
        if (command == null || command.trim().isEmpty()) return;
        
        commandHistory.remove(command);
        commandHistory.add(command);
        
        while (commandHistory.size() > maxHistory) {
            commandHistory.remove(0);
        }
    }
    
    public void addKnownCommand(String command) {
        if (!knownCommands.contains(command)) {
            knownCommands.add(command);
        }
    }
    
    public List<String> getHistory() {
        return new ArrayList<>(commandHistory);
    }
    
    public void setHistory(List<String> history) {
        this.commandHistory = new ArrayList<>(history);
    }
}
