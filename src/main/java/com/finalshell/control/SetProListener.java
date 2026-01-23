package com.finalshell.control;

/**
 * 设置Pro版本监听器接口
 * 用于处理Pro版本状态设置
 */
public interface SetProListener {
    
    /**
     * 设置Pro状态
     * @param isPro 是否为Pro版本
     * @param isValid 是否有效
     */
    void setProStatus(boolean isPro, boolean isValid);
    
    /**
     * 检查是否为Pro版本
     * @return true如果是Pro版本
     */
    boolean isPro();
    
    /**
     * 检查Pro状态是否有效
     * @return true如果有效
     */
    boolean isProValid();
}
