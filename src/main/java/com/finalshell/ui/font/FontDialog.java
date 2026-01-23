package com.finalshell.ui.font;

import javax.swing.*;
import java.awt.*;

/**
 * 字体选择对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Font_Menu_FullScreen_UI_DeepAnalysis.md - FontDialog
 */
public class FontDialog extends JDialog {
    
    private FontConfigPanel configPanel;
    private FontConfig result;
    private boolean confirmed = false;
    
    public FontDialog(Window owner) {
        this(owner, new FontConfig());
    }
    
    public FontDialog(Window owner, FontConfig config) {
        super(owner, "字体设置", ModalityType.APPLICATION_MODAL);
        
        configPanel = new FontConfigPanel(config);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        
        okButton.addActionListener(e -> {
            confirmed = true;
            result = configPanel.getFontConfig();
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        setLayout(new BorderLayout());
        add(configPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(400, 250);
        setLocationRelativeTo(owner);
    }
    
    public FontConfig showDialog() {
        setVisible(true);
        return confirmed ? result : null;
    }
    
    public static FontConfig showFontDialog(Window owner, FontConfig current) {
        FontDialog dialog = new FontDialog(owner, current != null ? current : new FontConfig());
        return dialog.showDialog();
    }
}
