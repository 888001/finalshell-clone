package com.finalshell.util;

import java.util.Comparator;

/**
 * 长整数比较器
 */
public class LongComparator implements Comparator<Object> {
    
    private boolean ascending = true;
    
    public LongComparator() {}
    
    public LongComparator(boolean ascending) {
        this.ascending = ascending;
    }
    
    @Override
    public int compare(Object o1, Object o2) {
        long v1 = toLong(o1);
        long v2 = toLong(o2);
        int result = Long.compare(v1, v2);
        return ascending ? result : -result;
    }
    
    private long toLong(Object obj) {
        if (obj == null) return 0L;
        if (obj instanceof Number) return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString().trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
