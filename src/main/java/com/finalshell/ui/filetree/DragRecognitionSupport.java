package com.finalshell.ui.filetree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * 拖拽识别支持类
 * 用于识别和处理组件的拖拽手势
 */
public class DragRecognitionSupport {
    
    private static int motionThreshold;
    private static DragRecognitionSupport instance;
    
    private Component component;
    private MouseEvent pressEvent;
    private boolean recognized;
    
    static {
        Integer threshold = (Integer) Toolkit.getDefaultToolkit()
                .getDesktopProperty("DnD.gestureMotionThreshold");
        motionThreshold = (threshold != null) ? threshold : 5;
    }
    
    public static DragRecognitionSupport getInstance() {
        if (instance == null) {
            instance = new DragRecognitionSupport();
        }
        return instance;
    }
    
    private DragRecognitionSupport() {
    }
    
    public boolean mousePressed(MouseEvent e) {
        component = (Component) e.getSource();
        pressEvent = e;
        recognized = false;
        return false;
    }
    
    public MouseEvent mouseReleased(MouseEvent e) {
        MouseEvent event = null;
        if (pressEvent != null && !recognized) {
            event = pressEvent;
        }
        pressEvent = null;
        return event;
    }
    
    public boolean mouseDragged(MouseEvent e, BeforeDrag beforeDrag) {
        if (recognized) {
            return true;
        }
        
        if (pressEvent != null && mapDragOperationFromModifiers(e) != TransferHandler.NONE) {
            int dx = Math.abs(e.getX() - pressEvent.getX());
            int dy = Math.abs(e.getY() - pressEvent.getY());
            
            if (dx > motionThreshold || dy > motionThreshold) {
                if (beforeDrag != null) {
                    beforeDrag.dragStarting(pressEvent);
                }
                recognized = true;
                return true;
            }
        }
        return false;
    }
    
    public static int mapDragOperationFromModifiers(MouseEvent e) {
        if (e == null) {
            return TransferHandler.NONE;
        }
        
        int modifiers = e.getModifiersEx();
        
        if ((modifiers & (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) 
                == (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) {
            return TransferHandler.LINK;
        } else if ((modifiers & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
            return TransferHandler.COPY;
        } else if ((modifiers & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
            return TransferHandler.MOVE;
        }
        
        return TransferHandler.COPY_OR_MOVE;
    }
    
    public static void setMotionThreshold(int threshold) {
        motionThreshold = threshold;
    }
    
    public static int getMotionThreshold() {
        return motionThreshold;
    }
    
    public interface BeforeDrag {
        void dragStarting(MouseEvent e);
    }
}
