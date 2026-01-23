package com.finalshell.script;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 计数器类
 * 用于脚本执行中的计数操作
 */
public class Counter {
    
    private AtomicLong value;
    private String name;
    private long step;
    private long min;
    private long max;
    
    public Counter() {
        this(0);
    }
    
    public Counter(long initialValue) {
        this.value = new AtomicLong(initialValue);
        this.step = 1;
        this.min = Long.MIN_VALUE;
        this.max = Long.MAX_VALUE;
    }
    
    public Counter(String name, long initialValue) {
        this(initialValue);
        this.name = name;
    }
    
    public long get() {
        return value.get();
    }
    
    public void set(long newValue) {
        value.set(clamp(newValue));
    }
    
    public long increment() {
        return add(step);
    }
    
    public long decrement() {
        return add(-step);
    }
    
    public long add(long delta) {
        long newValue = value.addAndGet(delta);
        if (newValue > max) {
            value.set(max);
            return max;
        } else if (newValue < min) {
            value.set(min);
            return min;
        }
        return newValue;
    }
    
    public void reset() {
        value.set(0);
    }
    
    public boolean compareAndSet(long expect, long update) {
        return value.compareAndSet(expect, clamp(update));
    }
    
    private long clamp(long val) {
        if (val > max) return max;
        if (val < min) return min;
        return val;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public long getStep() {
        return step;
    }
    
    public void setStep(long step) {
        this.step = step;
    }
    
    public long getMin() {
        return min;
    }
    
    public void setMin(long min) {
        this.min = min;
    }
    
    public long getMax() {
        return max;
    }
    
    public void setMax(long max) {
        this.max = max;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value.get());
    }
}
