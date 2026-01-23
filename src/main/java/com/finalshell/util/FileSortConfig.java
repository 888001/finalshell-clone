package com.finalshell.util;

import java.io.Serializable;

/**
 * 文件排序配置
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - FileSortConfig
 */
public class FileSortConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_SIZE = 1;
    public static final int SORT_BY_TIME = 2;
    public static final int SORT_BY_TYPE = 3;
    
    private int sortBy = SORT_BY_NAME;
    private boolean ascending = true;
    private boolean foldersFirst = true;
    private boolean showHiddenFiles = false;
    private boolean caseSensitive = false;
    
    public FileSortConfig() {}
    
    public FileSortConfig(int sortBy, boolean ascending) {
        this.sortBy = sortBy;
        this.ascending = ascending;
    }
    
    public int compare(String name1, long size1, long time1, boolean isDir1,
                       String name2, long size2, long time2, boolean isDir2) {
        // 目录优先
        if (foldersFirst && isDir1 != isDir2) {
            return isDir1 ? -1 : 1;
        }
        
        int result;
        switch (sortBy) {
            case SORT_BY_SIZE:
                result = Long.compare(size1, size2);
                break;
            case SORT_BY_TIME:
                result = Long.compare(time1, time2);
                break;
            case SORT_BY_TYPE:
                String ext1 = getExtension(name1);
                String ext2 = getExtension(name2);
                result = caseSensitive ? ext1.compareTo(ext2) : 
                    ext1.compareToIgnoreCase(ext2);
                if (result == 0) {
                    result = caseSensitive ? name1.compareTo(name2) :
                        name1.compareToIgnoreCase(name2);
                }
                break;
            case SORT_BY_NAME:
            default:
                result = caseSensitive ? name1.compareTo(name2) :
                    name1.compareToIgnoreCase(name2);
                break;
        }
        
        return ascending ? result : -result;
    }
    
    private String getExtension(String name) {
        int idx = name.lastIndexOf('.');
        return idx > 0 ? name.substring(idx + 1) : "";
    }
    
    public void toggleSort(int newSortBy) {
        if (sortBy == newSortBy) {
            ascending = !ascending;
        } else {
            sortBy = newSortBy;
            ascending = true;
        }
    }
    
    // Getters and Setters
    public int getSortBy() { return sortBy; }
    public void setSortBy(int sortBy) { this.sortBy = sortBy; }
    
    public boolean isAscending() { return ascending; }
    public void setAscending(boolean ascending) { this.ascending = ascending; }
    
    public boolean isFoldersFirst() { return foldersFirst; }
    public void setFoldersFirst(boolean foldersFirst) { this.foldersFirst = foldersFirst; }
    
    public boolean isShowHiddenFiles() { return showHiddenFiles; }
    public void setShowHiddenFiles(boolean showHiddenFiles) { this.showHiddenFiles = showHiddenFiles; }
    
    public boolean isCaseSensitive() { return caseSensitive; }
    public void setCaseSensitive(boolean caseSensitive) { this.caseSensitive = caseSensitive; }
    
    public String getSortByName() {
        switch (sortBy) {
            case SORT_BY_SIZE: return "大小";
            case SORT_BY_TIME: return "修改时间";
            case SORT_BY_TYPE: return "类型";
            default: return "名称";
        }
    }
}
