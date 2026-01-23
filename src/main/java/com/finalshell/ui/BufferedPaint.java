package com.finalshell.ui;

import java.awt.Graphics2D;

/**
 * 缓冲绘图回调接口
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md - BufferedPaint
 */
public interface BufferedPaint {
    
    /**
     * 绘制到缓冲区
     * @param g Graphics2D对象
     */
    void paintBuffer(Graphics2D g);
}
