package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 字体选择对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class FontDialog extends JDialog {
    
    private JList<String> fontList;
    private JList<String> styleList;
    private JList<String> sizeList;
    private JTextField previewField;
    private JButton confirmButton;
    private JButton cancelButton;
    
    private Font selectedFont;
    private boolean confirmed = false;
    
    private static final String[] STYLES = {"常规", "粗体", "斜体", "粗斜体"};
    private static final String[] SIZES = {"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "28", "32", "36", "48", "72"};
    
    public FontDialog(Frame owner) {
        super(owner, "选择字体", true);
        initUI();
        setSize(500, 400);
        setLocationRelativeTo(owner);
    }
    
    public FontDialog(Frame owner, Font initialFont) {
        this(owner);
        if (initialFont != null) {
            setSelectedFont(initialFont);
        }
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel listPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontList = new JList<>(fontNames);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.addListSelectionListener(e -> updatePreview());
        
        styleList = new JList<>(STYLES);
        styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleList.setSelectedIndex(0);
        styleList.addListSelectionListener(e -> updatePreview());
        
        sizeList = new JList<>(SIZES);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeList.setSelectedIndex(4);
        sizeList.addListSelectionListener(e -> updatePreview());
        
        JPanel fontPanel = new JPanel(new BorderLayout());
        fontPanel.add(new JLabel("字体:"), BorderLayout.NORTH);
        fontPanel.add(new JScrollPane(fontList), BorderLayout.CENTER);
        
        JPanel stylePanel = new JPanel(new BorderLayout());
        stylePanel.add(new JLabel("样式:"), BorderLayout.NORTH);
        stylePanel.add(new JScrollPane(styleList), BorderLayout.CENTER);
        
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.add(new JLabel("大小:"), BorderLayout.NORTH);
        sizePanel.add(new JScrollPane(sizeList), BorderLayout.CENTER);
        
        listPanel.add(fontPanel);
        listPanel.add(stylePanel);
        listPanel.add(sizePanel);
        
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("预览"));
        previewField = new JTextField("AaBbCcXxYyZz 中文预览");
        previewField.setEditable(false);
        previewField.setHorizontalAlignment(JTextField.CENTER);
        previewPanel.add(previewField, BorderLayout.CENTER);
        previewPanel.setPreferredSize(new Dimension(0, 80));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmButton = new JButton("确定");
        cancelButton = new JButton("取消");
        
        confirmButton.addActionListener(e -> confirm());
        cancelButton.addActionListener(e -> cancel());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(listPanel, BorderLayout.CENTER);
        mainPanel.add(previewPanel, BorderLayout.SOUTH);
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        if (fontList.getModel().getSize() > 0) {
            fontList.setSelectedIndex(0);
        }
    }
    
    private void updatePreview() {
        String fontName = fontList.getSelectedValue();
        int styleIndex = styleList.getSelectedIndex();
        String sizeStr = sizeList.getSelectedValue();
        
        if (fontName != null && sizeStr != null) {
            int style = Font.PLAIN;
            switch (styleIndex) {
                case 1: style = Font.BOLD; break;
                case 2: style = Font.ITALIC; break;
                case 3: style = Font.BOLD | Font.ITALIC; break;
            }
            
            int size = Integer.parseInt(sizeStr);
            selectedFont = new Font(fontName, style, size);
            previewField.setFont(selectedFont);
        }
    }
    
    private void setSelectedFont(Font font) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (int i = 0; i < fontList.getModel().getSize(); i++) {
            if (fontList.getModel().getElementAt(i).equals(font.getFamily())) {
                fontList.setSelectedIndex(i);
                fontList.ensureIndexIsVisible(i);
                break;
            }
        }
        
        int style = font.getStyle();
        if ((style & Font.BOLD) != 0 && (style & Font.ITALIC) != 0) {
            styleList.setSelectedIndex(3);
        } else if ((style & Font.BOLD) != 0) {
            styleList.setSelectedIndex(1);
        } else if ((style & Font.ITALIC) != 0) {
            styleList.setSelectedIndex(2);
        } else {
            styleList.setSelectedIndex(0);
        }
        
        for (int i = 0; i < sizeList.getModel().getSize(); i++) {
            if (Integer.parseInt(sizeList.getModel().getElementAt(i)) == font.getSize()) {
                sizeList.setSelectedIndex(i);
                sizeList.ensureIndexIsVisible(i);
                break;
            }
        }
        
        updatePreview();
    }
    
    private void confirm() {
        confirmed = true;
        dispose();
    }
    
    private void cancel() {
        confirmed = false;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Font getSelectedFont() {
        return selectedFont;
    }
}
