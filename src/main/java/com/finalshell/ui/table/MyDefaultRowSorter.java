package com.finalshell.ui.table;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;

/**
 * 自定义行排序器
 * 扩展TableRowSorter，提供自定义排序功能
 */
public class MyDefaultRowSorter<M extends TableModel> extends TableRowSorter<M> {
    
    private boolean caseSensitive = false;
    private boolean sortOnUpdates = true;
    
    public MyDefaultRowSorter() {
        super();
    }
    
    public MyDefaultRowSorter(M model) {
        super(model);
        initComparators();
    }
    
    private void initComparators() {
        // 设置默认比较器
        setComparator(0, new NumberComparator()); // 假设第一列是数字
    }
    
    @Override
    public void setModel(M model) {
        super.setModel(model);
        if (model != null) {
            initComparators();
        }
    }
    
    /**
     * 设置指定列的比较器
     */
    public void setColumnComparator(int column, Comparator<?> comparator) {
        setComparator(column, comparator);
    }
    
    /**
     * 设置是否区分大小写
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    public boolean isCaseSensitive() {
        return caseSensitive;
    }
    
    /**
     * 设置更新时是否排序
     */
    public void setSortOnUpdates(boolean sort) {
        this.sortOnUpdates = sort;
        super.setSortsOnUpdates(sort);
    }
    
    /**
     * 数字比较器
     */
    public static class NumberComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 == null && o2 == null) return 0;
            if (o1 == null) return -1;
            if (o2 == null) return 1;
            
            try {
                double d1 = toDouble(o1);
                double d2 = toDouble(o2);
                return Double.compare(d1, d2);
            } catch (NumberFormatException e) {
                return o1.toString().compareTo(o2.toString());
            }
        }
        
        private double toDouble(Object o) {
            if (o instanceof Number) {
                return ((Number) o).doubleValue();
            }
            String s = o.toString().replaceAll("[^0-9.-]", "");
            return s.isEmpty() ? 0 : Double.parseDouble(s);
        }
    }
    
    /**
     * 字符串比较器（忽略大小写）
     */
    public static class StringComparator implements Comparator<Object> {
        private final boolean caseSensitive;
        
        public StringComparator(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
        }
        
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 == null && o2 == null) return 0;
            if (o1 == null) return -1;
            if (o2 == null) return 1;
            
            String s1 = o1.toString();
            String s2 = o2.toString();
            
            if (caseSensitive) {
                return s1.compareTo(s2);
            } else {
                return s1.compareToIgnoreCase(s2);
            }
        }
    }
    
    /**
     * 大小比较器（如文件大小）
     */
    public static class SizeComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 == null && o2 == null) return 0;
            if (o1 == null) return -1;
            if (o2 == null) return 1;
            
            long s1 = parseSize(o1.toString());
            long s2 = parseSize(o2.toString());
            return Long.compare(s1, s2);
        }
        
        private long parseSize(String size) {
            size = size.trim().toUpperCase();
            try {
                if (size.endsWith("KB") || size.endsWith("K")) {
                    return (long) (Double.parseDouble(size.replaceAll("[^0-9.]", "")) * 1024);
                } else if (size.endsWith("MB") || size.endsWith("M")) {
                    return (long) (Double.parseDouble(size.replaceAll("[^0-9.]", "")) * 1024 * 1024);
                } else if (size.endsWith("GB") || size.endsWith("G")) {
                    return (long) (Double.parseDouble(size.replaceAll("[^0-9.]", "")) * 1024 * 1024 * 1024);
                } else if (size.endsWith("TB") || size.endsWith("T")) {
                    return (long) (Double.parseDouble(size.replaceAll("[^0-9.]", "")) * 1024L * 1024 * 1024 * 1024);
                } else {
                    return Long.parseLong(size.replaceAll("[^0-9]", ""));
                }
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}
