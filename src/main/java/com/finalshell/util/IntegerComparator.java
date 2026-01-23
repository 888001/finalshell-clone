package com.finalshell.util;

import java.util.Comparator;

/**
 * 整数比较器
 */
public class IntegerComparator implements Comparator<Object> {
    
    private boolean ascending = true;
    
    public IntegerComparator() {}
    
    public IntegerComparator(boolean ascending) {
        this.ascending = ascending;
    }
    
    @Override
    public int compare(Object o1, Object o2) {
        int v1 = toInt(o1);
        int v2 = toInt(o2);
        int result = Integer.compare(v1, v2);
        return ascending ? result : -result;
    }
    
    private int toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
