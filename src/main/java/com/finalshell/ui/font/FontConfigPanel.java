package com.finalshell.ui.font;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 字体配置面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Font_Menu_FullScreen_UI_DeepAnalysis.md - FontConfigPanel
 */
public class FontConfigPanel extends JPanel {
    
    private JComboBox<String> fontCombo;
    private JSpinner sizeSpinner;
    private JLabel previewLabel;
    private FontConfig fontConfig;
    private FontConfigListener listener;
    
    public FontConfigPanel() {
        this(new FontConfig());
    }
    
    public FontConfigPanel(FontConfig config) {
        this.fontConfig = config;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        updatePreview();
    }
    
    private void initComponents() {
        // 上部：字体选择
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 字体
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("字体:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        fontCombo = new JComboBox<>(FontSet.getInstance().getMonospaceFonts().toArray(new String[0]));
        fontCombo.setSelectedItem(fontConfig.getFontName());
        fontCombo.addActionListener(e -> {
            fontConfig.setFontName((String) fontCombo.getSelectedItem());
            updatePreview();
            notifyListener();
        });
        topPanel.add(fontCombo, gbc);
        
        // 大小
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        topPanel.add(new JLabel("大小:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        sizeSpinner = new JSpinner(new SpinnerNumberModel(fontConfig.getFontSize(), 8, 72, 1));
        sizeSpinner.addChangeListener(e -> {
            fontConfig.setFontSize((Integer) sizeSpinner.getValue());
            updatePreview();
            notifyListener();
        });
        topPanel.add(sizeSpinner, gbc);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中部：预览
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("预览"));
        
        previewLabel = new JLabel("AaBbCc 123 中文预览");
        previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        previewLabel.setPreferredSize(new Dimension(300, 80));
        previewPanel.add(previewLabel, BorderLayout.CENTER);
        
        add(previewPanel, BorderLayout.CENTER);
    }
    
    private void updatePreview() {
        Font font = fontConfig.toFont();
        previewLabel.setFont(font);
    }
    
    private void notifyListener() {
        if (listener != null) {
            listener.onFontChanged(fontConfig);
        }
    }
    
    public FontConfig getFontConfig() { return fontConfig; }
    
    public void setFontConfig(FontConfig config) {
        this.fontConfig = config;
        fontCombo.setSelectedItem(config.getFontName());
        sizeSpinner.setValue(config.getFontSize());
        updatePreview();
    }
    
    public void setListener(FontConfigListener listener) {
        this.listener = listener;
    }
    
    public interface FontConfigListener {
        void onFontChanged(FontConfig config);
    }
}
