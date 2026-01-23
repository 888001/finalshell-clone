package com.finalshell.util;

import java.io.*;
import java.util.*;

/**
 * 最近使用列表
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - RecentList
 */
public class RecentList<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private LinkedList<T> list;
    private int maxSize;
    
    public RecentList() {
        this(10);
    }
    
    public RecentList(int maxSize) {
        this.maxSize = maxSize;
        this.list = new LinkedList<>();
    }
    
    public synchronized void add(T item) {
        if (item == null) {
            return;
        }
        
        // 如果已存在，先移除
        list.remove(item);
        
        // 添加到头部
        list.addFirst(item);
        
        // 保持大小限制
        while (list.size() > maxSize) {
            list.removeLast();
        }
    }
    
    public synchronized T get(int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }
    
    public synchronized T getFirst() {
        return list.isEmpty() ? null : list.getFirst();
    }
    
    public synchronized T getLast() {
        return list.isEmpty() ? null : list.getLast();
    }
    
    public synchronized boolean remove(T item) {
        return list.remove(item);
    }
    
    public synchronized void clear() {
        list.clear();
    }
    
    public synchronized int size() {
        return list.size();
    }
    
    public synchronized boolean isEmpty() {
        return list.isEmpty();
    }
    
    public synchronized boolean contains(T item) {
        return list.contains(item);
    }
    
    public synchronized List<T> getAll() {
        return new ArrayList<>(list);
    }
    
    public synchronized List<T> getTop(int count) {
        List<T> result = new ArrayList<>();
        int limit = Math.min(count, list.size());
        for (int i = 0; i < limit; i++) {
            result.add(list.get(i));
        }
        return result;
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        while (list.size() > maxSize) {
            list.removeLast();
        }
    }
    
    @Override
    public String toString() {
        return list.toString();
    }
}
