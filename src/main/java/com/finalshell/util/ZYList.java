package com.finalshell.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义列表类
 * 扩展ArrayList，提供额外的便捷方法
 */
public class ZYList<E> extends ArrayList<E> {
    
    private static final long serialVersionUID = 1L;
    
    private int maxSize = -1;
    
    public ZYList() {
        super();
    }
    
    public ZYList(int initialCapacity) {
        super(initialCapacity);
    }
    
    public ZYList(Collection<? extends E> c) {
        super(c);
    }
    
    /**
     * 设置最大容量
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        trimToMaxSize();
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    @Override
    public boolean add(E e) {
        boolean result = super.add(e);
        trimToMaxSize();
        return result;
    }
    
    @Override
    public void add(int index, E element) {
        super.add(index, element);
        trimToMaxSize();
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = super.addAll(c);
        trimToMaxSize();
        return result;
    }
    
    /**
     * 添加到列表头部
     */
    public void addFirst(E e) {
        add(0, e);
    }
    
    /**
     * 添加到列表尾部
     */
    public void addLast(E e) {
        add(e);
    }
    
    /**
     * 获取第一个元素
     */
    public E getFirst() {
        return isEmpty() ? null : get(0);
    }
    
    /**
     * 获取最后一个元素
     */
    public E getLast() {
        return isEmpty() ? null : get(size() - 1);
    }
    
    /**
     * 移除第一个元素
     */
    public E removeFirst() {
        return isEmpty() ? null : remove(0);
    }
    
    /**
     * 移除最后一个元素
     */
    public E removeLast() {
        return isEmpty() ? null : remove(size() - 1);
    }
    
    /**
     * 移动元素到指定位置
     */
    public void moveTo(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= size() || toIndex < 0 || toIndex >= size()) {
            return;
        }
        E element = remove(fromIndex);
        add(toIndex, element);
    }
    
    /**
     * 交换两个元素
     */
    public void swap(int i, int j) {
        if (i < 0 || i >= size() || j < 0 || j >= size()) {
            return;
        }
        E temp = get(i);
        set(i, get(j));
        set(j, temp);
    }
    
    /**
     * 裁剪到最大容量
     */
    private void trimToMaxSize() {
        if (maxSize > 0) {
            while (size() > maxSize) {
                remove(size() - 1);
            }
        }
    }
    
    /**
     * 转换为普通List
     */
    public List<E> toList() {
        return new ArrayList<>(this);
    }
}
