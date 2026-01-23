package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * UI工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class UITools {
    
    public static void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        window.setLocation(x, y);
    }
    
    public static void setMinimumSize(Component component, int width, int height) {
        component.setMinimumSize(new Dimension(width, height));
    }
    
    public static void setPreferredSize(Component component, int width, int height) {
        component.setPreferredSize(new Dimension(width, height));
    }
    
    public static void setMaximumSize(Component component, int width, int height) {
        component.setMaximumSize(new Dimension(width, height));
    }
    
    public static void invokeOnEDT(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }
    
    public static void invokeAndWait(Runnable runnable) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeAndWait(runnable);
        }
    }
}
