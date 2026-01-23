package com.finalshell.theme;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 主题表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ThemeTableModel extends AbstractTableModel {
    
    private static final String[] COLUMNS = {"名称", "作者", "描述"};
    private List<ThemeInfo> themes;
    
    public ThemeTableModel() {
        this.themes = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return themes.size();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= themes.size()) {
            return null;
        }
        
        ThemeInfo theme = themes.get(rowIndex);
        switch (columnIndex) {
            case 0: return theme.getName();
            case 1: return theme.getAuthor();
            case 2: return theme.getDescription();
            default: return null;
        }
    }
    
    public void addTheme(ThemeInfo theme) {
        themes.add(theme);
        fireTableRowsInserted(themes.size() - 1, themes.size() - 1);
    }
    
    public void removeTheme(int row) {
        if (row >= 0 && row < themes.size()) {
            themes.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
    
    public ThemeInfo getThemeAt(int row) {
        if (row >= 0 && row < themes.size()) {
            return themes.get(row);
        }
        return null;
    }
    
    public void setThemes(List<ThemeInfo> themes) {
        this.themes = new ArrayList<>(themes);
        fireTableDataChanged();
    }
    
    public List<ThemeInfo> getThemes() {
        return new ArrayList<>(themes);
    }
}
