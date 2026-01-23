package com.finalshell.ui.config;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 终端配置面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: GlobalConfig_UI_DeepAnalysis.md - TerminalConfigPanel
 */
public class TerminalConfigPanel extends ConfigPanel {
    
    private JComboBox<String> charsetCombo;
    private JCheckBox commandPrompt;
    private JComboBox<String> backspaceCombo;
    private JComboBox<String> deleteCombo;
    private JSpinner scrollbackSpinner;
    
    public TerminalConfigPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 字符编码
        JPanel charsetPanel = createSection("字符编码");
        JPanel charsetRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        charsetRow.setOpaque(false);
        charsetRow.add(new JLabel("编码:"));
        
        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        charsetCombo = new JComboBox<>(charsets.keySet().toArray(new String[0]));
        charsetCombo.setSelectedItem("UTF-8");
        charsetRow.add(charsetCombo);
        charsetPanel.add(charsetRow);
        add(charsetPanel);
        
        add(Box.createVerticalStrut(10));
        
        // 按键配置
        JPanel keyPanel = createSection("按键配置");
        
        JPanel bsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bsRow.setOpaque(false);
        bsRow.add(new JLabel("Backspace:"));
        backspaceCombo = new JComboBox<>(new String[]{"ASCII - Backspace", "ASCII - Delete"});
        bsRow.add(backspaceCombo);
        keyPanel.add(bsRow);
        
        JPanel delRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        delRow.setOpaque(false);
        delRow.add(new JLabel("Delete:"));
        deleteCombo = new JComboBox<>(new String[]{"VT220 - Delete", "ASCII - Delete"});
        delRow.add(deleteCombo);
        keyPanel.add(delRow);
        
        add(keyPanel);
        
        add(Box.createVerticalStrut(10));
        
        // 缓冲区设置
        JPanel bufferPanel = createSection("缓冲区");
        JPanel scrollRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scrollRow.setOpaque(false);
        scrollRow.add(new JLabel("回滚行数:"));
        scrollbackSpinner = new JSpinner(new SpinnerNumberModel(10000, 1000, 100000, 1000));
        scrollRow.add(scrollbackSpinner);
        bufferPanel.add(scrollRow);
        add(bufferPanel);
        
        add(Box.createVerticalStrut(10));
        
        // 实验性功能
        JPanel expPanel = createSection("实验性功能");
        commandPrompt = new JCheckBox("终端命令提示");
        expPanel.add(commandPrompt);
        add(expPanel);
        
        add(Box.createVerticalGlue());
    }
    
    private JPanel createSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }
    
    @Override
    public void apply() {
        // TODO: 保存配置
    }
    
    @Override
    public void reset() {
        // TODO: 重置配置
    }
}
