package com.finalshell.terminal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 终端命令输入框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TerminalCmdAF extends AutoCompleteCmdAF {
    
    private CommandListener listener;
    private boolean sendOnEnter = true;
    
    public TerminalCmdAF() {
        super();
        initTerminalUI();
    }
    
    private void initTerminalUI() {
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        
        addActionListener(e -> {
            if (sendOnEnter) {
                sendCommand();
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_C:
                            if (listener != null) {
                                listener.onInterrupt();
                            }
                            e.consume();
                            break;
                        case KeyEvent.VK_L:
                            if (listener != null) {
                                listener.onClear();
                            }
                            e.consume();
                            break;
                    }
                }
            }
        });
    }
    
    private void sendCommand() {
        String cmd = getText().trim();
        if (!cmd.isEmpty()) {
            addToHistory(cmd);
            if (listener != null) {
                listener.onCommand(cmd);
            }
            setText("");
        }
    }
    
    public void setCommandListener(CommandListener listener) {
        this.listener = listener;
    }
    
    public void setSendOnEnter(boolean sendOnEnter) {
        this.sendOnEnter = sendOnEnter;
    }
    
    public interface CommandListener {
        void onCommand(String command);
        default void onInterrupt() {}
        default void onClear() {}
    }
}
