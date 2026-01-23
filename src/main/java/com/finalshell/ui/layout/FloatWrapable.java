package com.finalshell.ui.layout;

/**
 * 可浮动组件接口
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public interface FloatWrapable {
    
    void setFloating(boolean floating);
    
    boolean isFloating();
    
    String getTitle();
}
